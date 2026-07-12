-- ============================================================================
-- Dev/staging seed data: one example Debug Challenge (buggy code) on the
-- existing Two Sum problem, so the /debug-challenges page has something to
-- show and the whole flow can be tried end-to-end. Follows the same
-- prod-skip / idempotent pattern as V0082-V0084.
--
-- The bug: the two matched indices are printed in the wrong order
-- ("i, seen[complement]" instead of "seen[complement], i"), so e.g. for
-- input "2 7 11 15" / target "9" it prints "1 0" instead of the expected
-- "0 1". Findable and fixable by swapping the print arguments.
-- ============================================================================
DELIMITER $$
CREATE PROCEDURE `seed_example_debug_challenge_0086`()
BEGIN
    IF DATABASE() <> 'codepulse-prod' THEN
        IF NOT EXISTS (
            SELECT 1 FROM `problem_buggy_code`
            WHERE problem_id = 'b2b2b2b2-0001-4000-8000-000000000001' AND language = 'PYTHON3'
        ) THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES (
                'e6e60001-0000-4000-8000-000000000001',
                'b2b2b2b2-0001-4000-8000-000000000001',
                'PYTHON3',
                'nums = list(map(int, input().split()))\ntarget = int(input())\n\nseen = {}\nfor i, num in enumerate(nums):\n    complement = target - num\n    if complement in seen:\n        print(i, seen[complement])  # bug: indices are printed in the wrong order\n        break\n    seen[num] = i\n'
            );
        END IF;
    END IF;
END$$
DELIMITER ;

CALL seed_example_debug_challenge_0086();
DROP PROCEDURE seed_example_debug_challenge_0086;
