-- ============================================================================
-- "Debug Challenge" mode: an admin-authored solution with an intentional bug,
-- per problem per language, that a user starts from (instead of blank starter
-- code) and must find/fix. Reuses the existing problem/test_case/judge_submission
-- pipeline as-is — the fixed code is submitted through the same /api/judge/submit
-- endpoint and judged identically to a from-scratch solution.
-- ============================================================================
CREATE TABLE IF NOT EXISTS `problem_buggy_code` (
    id CHAR(36) PRIMARY KEY,
    problem_id CHAR(36) NOT NULL,
    language ENUM('C', 'CPP', 'JAVA', 'PYTHON3', 'JAVASCRIPT', 'TYPESCRIPT', 'GO', 'RUST', 'CSHARP', 'KOTLIN') NOT NULL,
    buggy_code MEDIUMTEXT NOT NULL,
    CONSTRAINT `uq_problemBuggyCode_problemId_language` UNIQUE (problem_id, language),
    CONSTRAINT `fk_problemBuggyCode_problemId_problem` FOREIGN KEY (problem_id) REFERENCES `problem`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
