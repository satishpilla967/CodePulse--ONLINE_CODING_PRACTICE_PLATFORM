-- The Discord-bot leaderboard/club integration (JDA) has been fully removed from the application.
-- Drop the tables that backed it. This is unrelated to the plain-password `Club` sign-up feature,
-- which remains in use.

DROP TABLE IF EXISTS `DiscordClubMetadata`;
DROP TABLE IF EXISTS `DiscordClub`;
