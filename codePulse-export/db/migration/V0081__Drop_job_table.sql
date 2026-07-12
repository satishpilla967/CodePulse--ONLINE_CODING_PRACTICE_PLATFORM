-- The Job table backed the LeetCode question-refetch queue (RefetchIncompleteQuestionsService ->
-- JobRepository -> JobNotifyHandler). Its only consumer, JobNotifyHandler, was already a documented
-- no-op after the switch to the internally-authored Judge0 judge (problems are authored directly
-- rather than synced from LeetCode), and RefetchIncompleteQuestionsService itself has now been
-- deleted since it was writing rows nobody reads. Drop the now-fully-unused table.

DROP TABLE IF EXISTS `Job`;
