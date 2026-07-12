-- Replace Discord OAuth login with email + password authentication.
-- discordId/discordName are no longer populated at signup (only via Discord club/bot
-- linking), so both must become nullable/optional. A new `email` column is the canonical
-- login identifier; `schoolEmail` remains a separate, optionally-verified field
-- used for school-tag enrollment and must not be reused for login.

ALTER TABLE `User`
    MODIFY `discordId` VARCHAR(32) NULL,
    MODIFY `discordName` VARCHAR(255) NULL COLLATE utf8mb4_0900_ai_ci;

ALTER TABLE `User`
    ADD COLUMN `email` VARCHAR(320) NULL AFTER `discordName`,
    ADD COLUMN `passwordHash` VARCHAR(255) NULL AFTER `email`;

ALTER TABLE `User`
    ADD CONSTRAINT `uq_user_email` UNIQUE (`email`);
