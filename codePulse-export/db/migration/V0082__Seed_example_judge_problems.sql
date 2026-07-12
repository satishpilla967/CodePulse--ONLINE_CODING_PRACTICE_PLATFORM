-- ============================================================================
-- Dev/staging seed data: a handful of simple example judge problems so the
-- online judge has something to solve on a fresh install. Mirrors the
-- prod-branching pattern from V0077's `seed_discord_clubs_0077` (DATABASE() =
-- 'codepulse-prod' check) to keep fixture data out of the production
-- database, and is written to be safe to re-run (checks for existing rows by
-- slug/problem before inserting, so re-applying this migration file, e.g.
-- after a `flyway repair`, does not create duplicates or fail).
-- ============================================================================
DELIMITER $$
CREATE PROCEDURE `seed_example_judge_problems_0082`()
BEGIN
    IF DATABASE() <> 'codepulse-prod' THEN

        -- --------------------------------------------------------------
        -- Two Sum
        -- --------------------------------------------------------------
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'two-sum') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES (
                'b2b2b2b2-0001-4000-8000-000000000001',
                'Two Sum',
                'two-sum',
                'Easy',
                'Given a line of space-separated integers representing an array `nums`, and a second line containing the integer `target`, print the 0-indexed positions of the two numbers that add up to `target`, separated by a space. Assume exactly one solution exists and you may not use the same element twice.',
                '2 <= nums.length <= 1000\n-10^9 <= nums[i] <= 10^9\n-10^9 <= target <= 10^9\nExactly one valid answer exists.',
                2000,
                128000
            );

            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order)
            VALUES
                ('b2b2b2b2-0002-4000-8000-000000000002', 'b2b2b2b2-0001-4000-8000-000000000001', '2 7 11 15\n9', '0 1', FALSE, 0),
                ('b2b2b2b2-0003-4000-8000-000000000003', 'b2b2b2b2-0001-4000-8000-000000000001', '3 2 4\n6', '1 2', FALSE, 1),
                ('b2b2b2b2-0004-4000-8000-000000000004', 'b2b2b2b2-0001-4000-8000-000000000001', '3 3\n6', '0 1', TRUE, 2);

            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code)
            VALUES
                ('b2b2b2b2-0005-4000-8000-000000000005', 'b2b2b2b2-0001-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\ntarget = int(input())\n\n# TODO: find the two indices that sum to target\n'),
                ('b2b2b2b2-0006-4000-8000-000000000006', 'b2b2b2b2-0001-4000-8000-000000000001', 'JAVASCRIPT',
                    'const lines = require("fs").readFileSync(0, "utf8").split("\\n");\nconst nums = lines[0].trim().split(" ").map(Number);\nconst target = parseInt(lines[1], 10);\n\n// TODO: find the two indices that sum to target\n');
        END IF;

        -- --------------------------------------------------------------
        -- Reverse a String
        -- --------------------------------------------------------------
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'reverse-a-string') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES (
                'b2b2b2b2-0011-4000-8000-000000000011',
                'Reverse a String',
                'reverse-a-string',
                'Easy',
                'Read a single line containing a string `s` and print it reversed.',
                '1 <= s.length <= 10^5\ns consists of printable ASCII characters.',
                2000,
                128000
            );

            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order)
            VALUES
                ('b2b2b2b2-0012-4000-8000-000000000012', 'b2b2b2b2-0011-4000-8000-000000000011', 'hello', 'olleh', FALSE, 0),
                ('b2b2b2b2-0013-4000-8000-000000000013', 'b2b2b2b2-0011-4000-8000-000000000011', 'CodePulse', 'esluPedoC', FALSE, 1),
                ('b2b2b2b2-0014-4000-8000-000000000014', 'b2b2b2b2-0011-4000-8000-000000000011', 'a', 'a', TRUE, 2);

            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code)
            VALUES
                ('b2b2b2b2-0015-4000-8000-000000000015', 'b2b2b2b2-0011-4000-8000-000000000011', 'PYTHON3',
                    's = input()\n\n# TODO: print the reversed string\n'),
                ('b2b2b2b2-0016-4000-8000-000000000016', 'b2b2b2b2-0011-4000-8000-000000000011', 'JAVASCRIPT',
                    'const s = require("fs").readFileSync(0, "utf8").trimEnd();\n\n// TODO: print the reversed string\n');
        END IF;

        -- --------------------------------------------------------------
        -- FizzBuzz
        -- --------------------------------------------------------------
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'fizzbuzz') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES (
                'b2b2b2b2-0021-4000-8000-000000000021',
                'FizzBuzz',
                'fizzbuzz',
                'Easy',
                'Read a single integer `n` and print the numbers from 1 to n, one per line. For multiples of 3 print "Fizz" instead of the number, for multiples of 5 print "Buzz", and for multiples of both print "FizzBuzz".',
                '1 <= n <= 10^4',
                2000,
                128000
            );

            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order)
            VALUES
                ('b2b2b2b2-0022-4000-8000-000000000022', 'b2b2b2b2-0021-4000-8000-000000000021', '3', '1\n2\nFizz', FALSE, 0),
                ('b2b2b2b2-0023-4000-8000-000000000023', 'b2b2b2b2-0021-4000-8000-000000000021', '5', '1\n2\nFizz\n4\nBuzz', FALSE, 1),
                ('b2b2b2b2-0024-4000-8000-000000000024', 'b2b2b2b2-0021-4000-8000-000000000021', '15', '1\n2\nFizz\n4\nBuzz\nFizz\n7\n8\nFizz\nBuzz\n11\nFizz\n13\n14\nFizzBuzz', TRUE, 2);

            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code)
            VALUES
                ('b2b2b2b2-0025-4000-8000-000000000025', 'b2b2b2b2-0021-4000-8000-000000000021', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print FizzBuzz from 1 to n\n'),
                ('b2b2b2b2-0026-4000-8000-000000000026', 'b2b2b2b2-0021-4000-8000-000000000021', 'JAVASCRIPT',
                    'const n = parseInt(require("fs").readFileSync(0, "utf8").trim(), 10);\n\n// TODO: print FizzBuzz from 1 to n\n');
        END IF;

        -- --------------------------------------------------------------
        -- Palindrome Check
        -- --------------------------------------------------------------
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'palindrome-check') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES (
                'b2b2b2b2-0031-4000-8000-000000000031',
                'Palindrome Check',
                'palindrome-check',
                'Easy',
                'Read a single line containing a string `s`. Print "true" if `s` reads the same forwards and backwards, and "false" otherwise. Comparison is case-sensitive and includes all characters.',
                '1 <= s.length <= 10^5',
                2000,
                128000
            );

            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order)
            VALUES
                ('b2b2b2b2-0032-4000-8000-000000000032', 'b2b2b2b2-0031-4000-8000-000000000031', 'racecar', 'true', FALSE, 0),
                ('b2b2b2b2-0033-4000-8000-000000000033', 'b2b2b2b2-0031-4000-8000-000000000031', 'hello', 'false', FALSE, 1),
                ('b2b2b2b2-0034-4000-8000-000000000034', 'b2b2b2b2-0031-4000-8000-000000000031', 'a', 'true', TRUE, 2);

            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code)
            VALUES
                ('b2b2b2b2-0035-4000-8000-000000000035', 'b2b2b2b2-0031-4000-8000-000000000031', 'PYTHON3',
                    's = input()\n\n# TODO: print "true" or "false"\n'),
                ('b2b2b2b2-0036-4000-8000-000000000036', 'b2b2b2b2-0031-4000-8000-000000000031', 'JAVASCRIPT',
                    'const s = require("fs").readFileSync(0, "utf8").trimEnd();\n\n// TODO: print "true" or "false"\n');
        END IF;

    END IF;
END$$
DELIMITER ;

CALL `seed_example_judge_problems_0082`();
DROP PROCEDURE `seed_example_judge_problems_0082`;
