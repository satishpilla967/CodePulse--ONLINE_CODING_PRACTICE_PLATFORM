-- Consolidated MySQL-native schema.
--
-- This migration replaces V0001-V0076 (archived under db/migration/archive-postgres/,
-- see infra/README.md), which were written for PostgreSQL and are no longer scanned
-- by Flyway. It creates the full, current schema (the union/latest-state of all prior
-- Postgres migrations) directly in MySQL syntax for the Postgres -> MySQL migration.
--
-- Mechanical translations applied:
--   * UUID columns              -> CHAR(36)
--   * TIMESTAMP WITH TIME ZONE  -> TIMESTAMP (app already normalizes to UTC in Java)
--   * ON DELETE/UPDATE CASCADE  -> unchanged (InnoDB supports this natively)
--   * Postgres native CREATE TYPE ... AS ENUM -> inline column-scoped MySQL ENUM(...)
--   * gen_random_uuid() seed data -> literal pre-generated UUID string constants
--   * pgcrypto extension        -> dropped entirely (no longer needed)
--   * job/lobby LISTEN/NOTIFY triggers -> dropped (replaced by Redis pub/sub, see
--     NotifyListener.java); underlying table structures are preserved below.
--
-- Case-insensitive search: columns that are queried with ILIKE in the Java layer
-- (User.discordName/leetcodeUsername/nickname, Question.questionTitle,
-- Leaderboard.name) use utf8mb4_0900_ai_ci collation so a plain LIKE behaves
-- case-insensitively, matching current Postgres ILIKE behavior.

SET default_storage_engine = InnoDB;

-- ============================================================================
-- User
-- ============================================================================
CREATE TABLE IF NOT EXISTS `User` (
    id CHAR(36) PRIMARY KEY,
    `discordId` VARCHAR(32) NOT NULL,
    `discordName` VARCHAR(255) NOT NULL COLLATE utf8mb4_0900_ai_ci,
    `leetcodeUsername` VARCHAR(128) NULL COLLATE utf8mb4_0900_ai_ci,
    `nickname` VARCHAR(255) NULL COLLATE utf8mb4_0900_ai_ci,
    `verifyKey` TEXT NULL,
    `admin` BOOLEAN NOT NULL DEFAULT FALSE,
    `schoolEmail` VARCHAR(320) NULL,
    `profileUrl` VARCHAR(255) NULL,
    CONSTRAINT `uq_user_discordId` UNIQUE (`discordId`),
    CONSTRAINT `uq_user_leetcodeUsername` UNIQUE (`leetcodeUsername`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================================
-- Session
-- ============================================================================
CREATE TABLE IF NOT EXISTS `Session` (
    id VARCHAR(64) PRIMARY KEY,
    `expiresAt` TIMESTAMP NOT NULL,
    `userId` CHAR(36) NOT NULL,
    CONSTRAINT `fk_session_user` FOREIGN KEY (`userId`) REFERENCES `User`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Question
-- ============================================================================
CREATE TABLE IF NOT EXISTS `Question` (
    id CHAR(36) PRIMARY KEY,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `questionSlug` VARCHAR(256) NOT NULL,
    `questionDifficulty` ENUM('Easy', 'Medium', 'Hard') NOT NULL,
    `questionNumber` SMALLINT NOT NULL,
    `questionLink` TEXT NOT NULL,
    `pointsAwarded` INTEGER NULL,
    `userId` CHAR(36) NOT NULL,
    `questionTitle` TEXT NOT NULL,
    `description` TEXT NULL,
    `acceptanceRate` FLOAT NOT NULL CHECK (`acceptanceRate` > 0),
    `submittedAt` TIMESTAMP NOT NULL,
    `runtime` VARCHAR(10) NULL,
    `memory` VARCHAR(10) NULL,
    `code` TEXT NULL,
    `language` TEXT NULL,
    `submissionId` VARCHAR(255) NULL,
    CONSTRAINT `uq_question_submissionId` UNIQUE (`submissionId`),
    CONSTRAINT `fk_question_user` FOREIGN KEY (`userId`) REFERENCES `User`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- questionTitle needs case-insensitive collation for ILIKE-equivalent search.
ALTER TABLE `Question` MODIFY `questionTitle` TEXT COLLATE utf8mb4_0900_ai_ci NOT NULL;

-- ============================================================================
-- Leaderboard
-- ============================================================================
CREATE TABLE IF NOT EXISTS `Leaderboard` (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(1024) NOT NULL COLLATE utf8mb4_0900_ai_ci,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deletedAt` TIMESTAMP NULL,
    `shouldExpireBy` TIMESTAMP NULL,
    `syntaxHighlightingLanguage` TEXT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Metadata
-- ============================================================================
CREATE TABLE IF NOT EXISTS `Metadata` (
    id CHAR(36) PRIMARY KEY,
    `userId` CHAR(36) NOT NULL,
    `leaderboardId` CHAR(36) NOT NULL,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `totalScore` INTEGER NOT NULL DEFAULT 0 CHECK (`totalScore` >= 0),
    CONSTRAINT `fk_metadata_user` FOREIGN KEY (`userId`) REFERENCES `User`(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_metadata_leaderboard` FOREIGN KEY (`leaderboardId`) REFERENCES `Leaderboard`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- POTD
-- ============================================================================
CREATE TABLE IF NOT EXISTS `POTD` (
    id CHAR(36) NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    multiplier FLOAT NOT NULL CHECK (multiplier BETWEEN 1 AND 5),
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Auth
-- ============================================================================
CREATE TABLE IF NOT EXISTS `Auth` (
    id CHAR(36) PRIMARY KEY NOT NULL,
    token TEXT NOT NULL,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    csrf TEXT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- UserTag (Tag enum, union of all ALTER TYPE ADD VALUE calls; column-scoped so
-- the historical duplicate-named "Tag" type collision between V0023/V0035
-- naturally disappears here)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `UserTag` (
    id CHAR(36) PRIMARY KEY,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `userId` CHAR(36) NOT NULL,
    tag ENUM('Patina', 'Hunter', 'Nyu', 'Baruch', 'Rpi', 'Gwc', 'Sbu', 'Ccny', 'Columbia', 'Cornell', 'Bmcc', 'MHCPlusPlus') NOT NULL,
    CONSTRAINT `pk_user_tags` UNIQUE (`userId`, tag),
    CONSTRAINT `fk_userTag_user` FOREIGN KEY (`userId`) REFERENCES `User`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- WeeklyMessage
-- ============================================================================
CREATE TABLE IF NOT EXISTS `WeeklyMessage` (
    id CHAR(36) PRIMARY KEY NOT NULL,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Announcement
-- ============================================================================
CREATE TABLE IF NOT EXISTS `Announcement` (
    id CHAR(36) PRIMARY KEY NOT NULL,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `expiresAt` TIMESTAMP NOT NULL,
    `showTimer` BOOLEAN DEFAULT FALSE,
    message TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Achievement (place/leaderboard enum columns added in V0051)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `Achievement` (
    id CHAR(36) PRIMARY KEY,
    `userId` CHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    `isActive` BOOLEAN DEFAULT TRUE,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deletedAt` TIMESTAMP NULL,
    place ENUM('ONE', 'TWO', 'THREE') NOT NULL,
    leaderboard ENUM('Patina', 'Hunter', 'Nyu', 'Baruch', 'Rpi', 'Gwc', 'Sbu', 'Ccny', 'Columbia', 'Cornell', 'Bmcc', 'MHCPlusPlus') NULL,
    CONSTRAINT `fk_achievement_user` FOREIGN KEY (`userId`) REFERENCES `User`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Club
-- ============================================================================
CREATE TABLE IF NOT EXISTS `Club` (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    slug VARCHAR(255) NOT NULL,
    `splashIconUrl` VARCHAR(500) NULL,
    password VARCHAR(255) NOT NULL,
    tag ENUM('Patina', 'Hunter', 'Nyu', 'Baruch', 'Rpi', 'Gwc', 'Sbu', 'Ccny', 'Columbia', 'Cornell', 'Bmcc', 'MHCPlusPlus') NOT NULL,
    CONSTRAINT `uq_club_slug` UNIQUE (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- ApiKey / ApiKeyAccess
-- ============================================================================
CREATE TABLE IF NOT EXISTS `ApiKey` (
    id CHAR(36) PRIMARY KEY,
    `apiKeyHash` TEXT NOT NULL,
    `expiresAt` TIMESTAMP NOT NULL,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedBy` CHAR(36) NOT NULL,
    CONSTRAINT `uq_apiKey_apiKeyHash` UNIQUE (`apiKeyHash`(255)),
    CONSTRAINT `fk_apiKey_updatedBy_user` FOREIGN KEY (`updatedBy`) REFERENCES `User`(id) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `ApiKeyAccess` (
    id CHAR(36) PRIMARY KEY,
    `apiKeyId` CHAR(36) NOT NULL,
    access ENUM('GWC_READ_BY_USER', 'TEST_VALUE') NOT NULL,
    CONSTRAINT `uq_api_key_access_pair` UNIQUE (`apiKeyId`, access),
    CONSTRAINT `fk_api_key_access_key` FOREIGN KEY (`apiKeyId`) REFERENCES `ApiKey`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- QuestionBank (created before QuestionTopic references it)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `QuestionBank` (
    id CHAR(36) PRIMARY KEY,
    `questionSlug` VARCHAR(256) NOT NULL,
    `questionDifficulty` ENUM('Easy', 'Medium', 'Hard') NOT NULL,
    `questionLink` TEXT NOT NULL,
    `questionNumber` SMALLINT NOT NULL,
    `questionTitle` TEXT NOT NULL,
    description TEXT NULL,
    `acceptanceRate` REAL NOT NULL,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- QuestionTopic (LeetcodeTopicEnum inline; questionId/questionBankId nullable
-- per V0056, exactly one must be non-null)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `QuestionTopic` (
    id CHAR(36) PRIMARY KEY,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `questionId` CHAR(36) NULL,
    `questionBankId` CHAR(36) NULL,
    `topicSlug` TEXT NOT NULL,
    `topic` ENUM(
        'stack','data-stream','rejection-sampling','geometry','counting','design',
        'probability-and-statistics','minimum-spanning-tree','line-sweep','number-theory',
        'rolling-hash','segment-tree','biconnected-component','monotonic-stack','iterator',
        'queue','radix-sort','bucket-sort','shell','memoization','string','prefix-sum',
        'concurrency','database','shortest-path','sorting','linked-list','sliding-window',
        'suffix-array','doubly-linked-list','simulation','ordered-set','graph','math',
        'ordered-map','game-theory','dynamic-programming','recursion','monotonic-queue',
        'matrix','reservoir-sampling','merge-sort','combinatorics','interactive','binary-tree',
        'randomized','bitmask','breadth-first-search','string-matching','greedy','brainteaser',
        'backtracking','bit-manipulation','union-find','binary-search-tree','two-pointers',
        'array','depth-first-search','eulerian-circuit','tree','binary-search',
        'strongly-connected-component','enumeration','heap-priority-queue','divide-and-conquer',
        'hash-function','hash-table','trie','topological-sort','quickselect','binary-indexed-tree',
        'counting-sort','unknown'
    ) NOT NULL,
    CONSTRAINT `pk_question_topic` UNIQUE (`questionId`, `questionBankId`, `topic`),
    CONSTRAINT `fk_question_topic_question` FOREIGN KEY (`questionId`) REFERENCES `Question`(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_question_bank` FOREIGN KEY (`questionBankId`) REFERENCES `QuestionBank`(id) ON DELETE CASCADE ON UPDATE CASCADE
    -- NOTE: MySQL forbids a column from being used in both a CHECK constraint and as the
    -- referencing column of a cascading foreign key (error 3823), so the "exactly one of
    -- questionId/questionBankId must be non-null" invariant (enforced in Postgres via a CHECK)
    -- is enforced at the application layer instead (QuestionTopicSqlRepository only ever sets
    -- one or the other when inserting).
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Job (trigger/LISTEN-NOTIFY dropped; replaced by Redis pub/sub, see
-- NotifyListener.java / job_channel publishes in JobSqlRepository.java)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `Job` (
    id CHAR(36) PRIMARY KEY NOT NULL,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `processedAt` TIMESTAMP NULL,
    `completedAt` TIMESTAMP NULL,
    `nextAttemptAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('COMPLETE', 'PROCESSING', 'INCOMPLETE') NOT NULL DEFAULT 'INCOMPLETE',
    `questionId` VARCHAR(255) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Lobby (trigger/LISTEN-NOTIFY dropped; replaced by Redis pub/sub, see
-- NotifyListener.java / lobby_channel publishes in LobbySqlRepository.java)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `Lobby` (
    id CHAR(36) PRIMARY KEY,
    `joinCode` TEXT NOT NULL,
    status ENUM('CLOSED', 'AVAILABLE', 'ACTIVE', 'COMPLETED') NOT NULL,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `expiresAt` TIMESTAMP NULL,
    `playerCount` INTEGER NOT NULL DEFAULT 0 CHECK (`playerCount` IN (0, 1, 2)),
    `winnerId` CHAR(36) NULL,
    tie BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT `fk_lobby_winnerId_user` FOREIGN KEY (`winnerId`) REFERENCES `User`(id) ON DELETE CASCADE ON UPDATE CASCADE
    -- NOTE: MySQL forbids a column from being used in both a CHECK constraint and as the
    -- referencing column of a cascading foreign key (error 3823), so the "tie=true implies
    -- winnerId IS NULL" invariant (enforced in Postgres via a CHECK) is enforced at the
    -- application layer instead (DuelManager sets winnerId=null whenever tie=true).
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- LobbyPlayer
-- ============================================================================
CREATE TABLE IF NOT EXISTS `LobbyPlayer` (
    id CHAR(36) PRIMARY KEY,
    `lobbyId` CHAR(36) NOT NULL,
    `playerId` CHAR(36) NOT NULL,
    points INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT `fk_lobbyPlayer_playerId_user` FOREIGN KEY (`playerId`) REFERENCES `User`(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_lobbyPlayer_lobbyId_user` FOREIGN KEY (`lobbyId`) REFERENCES `Lobby`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- LobbyPlayerQuestion
-- ============================================================================
CREATE TABLE IF NOT EXISTS `LobbyPlayerQuestion` (
    id CHAR(36) PRIMARY KEY,
    `lobbyPlayerId` CHAR(36) NOT NULL,
    `questionId` CHAR(36) NULL,
    points INTEGER NULL,
    CONSTRAINT `uq_questionId_and_lobbyPlayerId` UNIQUE (`lobbyPlayerId`, `questionId`),
    CONSTRAINT `fk_lobbyPlayerQuestion_lobbyPlayerId_user` FOREIGN KEY (`lobbyPlayerId`) REFERENCES `LobbyPlayer`(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_lobbyPlayerQuestion_questionId_user` FOREIGN KEY (`questionId`) REFERENCES `Question`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- LobbyQuestion
-- ============================================================================
CREATE TABLE IF NOT EXISTS `LobbyQuestion` (
    id CHAR(36) PRIMARY KEY,
    `lobbyId` CHAR(36) NOT NULL,
    `questionBankId` CHAR(36) NOT NULL,
    `userSolvedCount` INTEGER NOT NULL DEFAULT 0,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_lobbyQuestion_lobbyId_user` FOREIGN KEY (`lobbyId`) REFERENCES `Lobby`(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_lobbyQuestion_questionBankId_user` FOREIGN KEY (`questionBankId`) REFERENCES `QuestionBank`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- DiscordClub / DiscordClubMetadata
-- ============================================================================
CREATE TABLE IF NOT EXISTS `DiscordClub` (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NULL,
    tag ENUM('Patina', 'Hunter', 'Nyu', 'Baruch', 'Rpi', 'Gwc', 'Sbu', 'Ccny', 'Columbia', 'Cornell', 'Bmcc', 'MHCPlusPlus') NOT NULL,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deletedAt` TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `DiscordClubMetadata` (
    id CHAR(36) NOT NULL PRIMARY KEY,
    `guildId` TEXT NULL,
    `leaderboardChannelId` TEXT NULL,
    `discordClubId` CHAR(36) NOT NULL,
    CONSTRAINT `fk_discordClub` FOREIGN KEY (`discordClubId`) REFERENCES `DiscordClub`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- BackgroundTask
-- ============================================================================
CREATE TABLE IF NOT EXISTS `BackgroundTask` (
    id CHAR(36) PRIMARY KEY,
    task ENUM('LEETCODE_QUESTION_BANK', 'USER_METRICS') NOT NULL,
    `completedAt` TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Report (feedback table)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `Report` (
    id CHAR(36) PRIMARY KEY NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    email VARCHAR(255) NULL,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- UserMetrics
-- ============================================================================
CREATE TABLE IF NOT EXISTS `UserMetrics` (
    id CHAR(36) NOT NULL PRIMARY KEY,
    `userId` CHAR(36) NOT NULL,
    points INTEGER NOT NULL,
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deletedAt` TIMESTAMP NULL,
    CONSTRAINT `fk_userMetrics_user` FOREIGN KEY (`userId`) REFERENCES `User`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Seed data (V0060/V0070/V0073 equivalents). gen_random_uuid() replaced with
-- literal pre-generated UUID string constants. The Postgres originals branched
-- on current_database() = 'codepulse-prod' to pick prod vs. non-prod Discord
-- guild/channel IDs for DiscordClubMetadata; this is real per-environment
-- production configuration (not dev/test fixture data), so the branching is
-- preserved via a one-off stored procedure using MySQL's DATABASE() function
-- (Flyway's MySQL parser understands DELIMITER changes in migration scripts).
-- ============================================================================
DELIMITER $$
CREATE PROCEDURE `seed_discord_clubs_0077`()
BEGIN
    -- Patina Network (V0060)
    INSERT INTO `DiscordClub` (id, name, description, tag)
    VALUES ('a1a1a1a1-0001-4000-8000-000000000001', 'Patina Network', NULL, 'Patina');

    IF DATABASE() = 'codepulse-prod' THEN
        INSERT INTO `DiscordClubMetadata` (id, `guildId`, `leaderboardChannelId`, `discordClubId`)
        VALUES ('a1a1a1a1-0002-4000-8000-000000000002', '1246865559019065505', '1320473999989538956', 'a1a1a1a1-0001-4000-8000-000000000001');
    ELSE
        INSERT INTO `DiscordClubMetadata` (id, `guildId`, `leaderboardChannelId`, `discordClubId`)
        VALUES ('a1a1a1a1-0002-4000-8000-000000000002', '1389762654452580373', '1401739528057655436', 'a1a1a1a1-0001-4000-8000-000000000001');
    END IF;

    -- MHC++ (V0070)
    INSERT INTO `DiscordClub` (id, name, description, tag)
    VALUES ('a1a1a1a1-0003-4000-8000-000000000003', 'MHC++', NULL, 'MHCPlusPlus');

    IF DATABASE() = 'codepulse-prod' THEN
        INSERT INTO `DiscordClubMetadata` (id, `guildId`, `leaderboardChannelId`, `discordClubId`)
        VALUES ('a1a1a1a1-0004-4000-8000-000000000004', '1048355383728672829', '1074033436404228206', 'a1a1a1a1-0003-4000-8000-000000000003');
    ELSE
        INSERT INTO `DiscordClubMetadata` (id, `guildId`, `leaderboardChannelId`, `discordClubId`)
        VALUES ('a1a1a1a1-0004-4000-8000-000000000004', '1389762654452580373', '1463703700697518113', 'a1a1a1a1-0003-4000-8000-000000000003');
    END IF;

    -- GWC - Hunter College (V0073)
    INSERT INTO `DiscordClub` (id, name, description, tag)
    VALUES ('a1a1a1a1-0005-4000-8000-000000000005', 'GWC - Hunter College', NULL, 'Gwc');

    IF DATABASE() = 'codepulse-prod' THEN
        INSERT INTO `DiscordClubMetadata` (id, `guildId`, `leaderboardChannelId`, `discordClubId`)
        VALUES ('a1a1a1a1-0006-4000-8000-000000000006', '1066177903345278986', '1468417251274129452', 'a1a1a1a1-0005-4000-8000-000000000005');
    ELSE
        INSERT INTO `DiscordClubMetadata` (id, `guildId`, `leaderboardChannelId`, `discordClubId`)
        VALUES ('a1a1a1a1-0006-4000-8000-000000000006', '1389762654452580373', '1463703700697518113', 'a1a1a1a1-0005-4000-8000-000000000005');
    END IF;
END$$
DELIMITER ;

CALL `seed_discord_clubs_0077`();
DROP PROCEDURE `seed_discord_clubs_0077`;
