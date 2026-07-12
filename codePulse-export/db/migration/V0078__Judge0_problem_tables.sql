-- ============================================================================
-- Phase B: Judge0-backed online judge data model.
-- New, decoupled tables: problem / problem_starter_code / test_case /
-- judge_submission / judge_submission_result.
-- ============================================================================

CREATE TABLE IF NOT EXISTS `problem` (
    id CHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    difficulty ENUM('Easy', 'Medium', 'Hard') NOT NULL,
    statement MEDIUMTEXT NOT NULL,
    constraints TEXT NULL,
    time_limit_ms INTEGER NOT NULL DEFAULT 2000,
    memory_limit_kb INTEGER NOT NULL DEFAULT 128000,
    created_by CHAR(36) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_problem_createdBy_user` FOREIGN KEY (created_by) REFERENCES `User`(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `problem_starter_code` (
    id CHAR(36) PRIMARY KEY,
    problem_id CHAR(36) NOT NULL,
    language ENUM('C', 'CPP', 'JAVA', 'PYTHON3', 'JAVASCRIPT', 'TYPESCRIPT', 'GO', 'RUST', 'CSHARP', 'KOTLIN') NOT NULL,
    starter_code MEDIUMTEXT NOT NULL,
    CONSTRAINT `uq_problemStarterCode_problemId_language` UNIQUE (problem_id, language),
    CONSTRAINT `fk_problemStarterCode_problemId_problem` FOREIGN KEY (problem_id) REFERENCES `problem`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `test_case` (
    id CHAR(36) PRIMARY KEY,
    problem_id CHAR(36) NOT NULL,
    input MEDIUMTEXT NOT NULL,
    expected_output MEDIUMTEXT NOT NULL,
    is_hidden BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT `fk_testCase_problemId_problem` FOREIGN KEY (problem_id) REFERENCES `problem`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `judge_submission` (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    problem_id CHAR(36) NOT NULL,
    lobby_id CHAR(36) NULL,
    language ENUM('C', 'CPP', 'JAVA', 'PYTHON3', 'JAVASCRIPT', 'TYPESCRIPT', 'GO', 'RUST', 'CSHARP', 'KOTLIN') NOT NULL,
    source_code MEDIUMTEXT NOT NULL,
    status ENUM('PENDING', 'RUNNING', 'ACCEPTED', 'WRONG_ANSWER', 'TIME_LIMIT_EXCEEDED',
        'MEMORY_LIMIT_EXCEEDED', 'RUNTIME_ERROR', 'COMPILE_ERROR', 'INTERNAL_ERROR') NOT NULL DEFAULT 'PENDING',
    test_cases_passed INTEGER NOT NULL DEFAULT 0,
    test_cases_total INTEGER NOT NULL DEFAULT 0,
    max_runtime_ms INTEGER NULL,
    max_memory_kb INTEGER NULL,
    points_awarded INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    CONSTRAINT `fk_judgeSubmission_userId_user` FOREIGN KEY (user_id) REFERENCES `User`(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_judgeSubmission_problemId_problem` FOREIGN KEY (problem_id) REFERENCES `problem`(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_judgeSubmission_lobbyId_lobby` FOREIGN KEY (lobby_id) REFERENCES `Lobby`(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX `idx_judgeSubmission_user_problem` (user_id, problem_id),
    INDEX `idx_judgeSubmission_status` (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `judge_submission_result` (
    id CHAR(36) PRIMARY KEY,
    judge_submission_id CHAR(36) NOT NULL,
    test_case_id CHAR(36) NOT NULL,
    judge0_token VARCHAR(64) NULL,
    status ENUM('PENDING', 'RUNNING', 'ACCEPTED', 'WRONG_ANSWER', 'TIME_LIMIT_EXCEEDED',
        'MEMORY_LIMIT_EXCEEDED', 'RUNTIME_ERROR', 'COMPILE_ERROR', 'INTERNAL_ERROR') NOT NULL DEFAULT 'PENDING',
    stdout MEDIUMTEXT NULL,
    stderr MEDIUMTEXT NULL,
    runtime_ms INTEGER NULL,
    memory_kb INTEGER NULL,
    CONSTRAINT `fk_judgeSubmissionResult_submissionId_judgeSubmission` FOREIGN KEY (judge_submission_id) REFERENCES `judge_submission`(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_judgeSubmissionResult_testCaseId_testCase` FOREIGN KEY (test_case_id) REFERENCES `test_case`(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX `idx_judgeSubmissionResult_submissionId` (judge_submission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- LobbyQuestion currently references QuestionBank; add a nullable problem_id
-- pointer so duels can be assigned an internally-authored problem instead of
-- a LeetCode-sourced QuestionBank row. questionBankId is left in place
-- (made nullable) rather than dropped outright to avoid a destructive change
-- to existing duel history in this pass; DuelManager now sources new lobbies
-- from `problem`.
ALTER TABLE `LobbyQuestion`
    MODIFY COLUMN `questionBankId` CHAR(36) NULL,
    ADD COLUMN `problemId` CHAR(36) NULL AFTER `questionBankId`,
    ADD CONSTRAINT `fk_lobbyQuestion_problemId_problem` FOREIGN KEY (`problemId`) REFERENCES `problem`(id) ON DELETE CASCADE ON UPDATE CASCADE;
