-- profileUrl previously only ever held an external avatar URL (varchar(255)). Profile picture upload
-- now stores the image directly as a base64 data URI (no object storage credentials are configured for
-- this deployment yet), which is far larger than any URL, so the column needs to hold much more text.
ALTER TABLE `User` MODIFY COLUMN `profileUrl` MEDIUMTEXT NULL;
