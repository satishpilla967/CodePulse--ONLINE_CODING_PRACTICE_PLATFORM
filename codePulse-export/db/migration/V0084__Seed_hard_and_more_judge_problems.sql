-- ============================================================================
-- Dev/staging seed data: expands the judge problem catalog from 50 to 100,
-- this time including Hard difficulty (DP, graphs, backtracking). Follows
-- the same prod-skip / idempotent pattern as V0082/V0083. PYTHON3 starter
-- code only, same trade-off as V0083.
-- ============================================================================
DELIMITER $$
CREATE PROCEDURE `seed_hard_and_more_judge_problems_0084`()
BEGIN
    IF DATABASE() <> 'codepulse-prod' THEN

        -- Count Consonants
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'count-consonants') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50001-0000-4000-8000-000000000001', 'Count Consonants', 'count-consonants', 'Easy',
                'Given a line of lowercase letters and spaces, print the number of consonants (letters that are not a, e, i, o, u).',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50001-0000-4000-8000-000000000002', 'd5d50001-0000-4000-8000-000000000001', 'hello world', '7', FALSE, 0),
                ('d5d50001-0000-4000-8000-000000000003', 'd5d50001-0000-4000-8000-000000000001', 'programming', '8', FALSE, 1),
                ('d5d50001-0000-4000-8000-000000000004', 'd5d50001-0000-4000-8000-000000000001', 'bcd', '3', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50001-0000-4000-8000-000000000005', 'd5d50001-0000-4000-8000-000000000001', 'PYTHON3',
                    'text = input()\n\n# TODO: print the number of consonants\n');
        END IF;

        -- Sum of Squares
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'sum-of-squares') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50002-0000-4000-8000-000000000001', 'Sum of Squares', 'sum-of-squares', 'Easy',
                'Given a line of space-separated integers, print the sum of their squares.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50002-0000-4000-8000-000000000002', 'd5d50002-0000-4000-8000-000000000001', '1 2 3', '14', FALSE, 0),
                ('d5d50002-0000-4000-8000-000000000003', 'd5d50002-0000-4000-8000-000000000001', '2 2', '8', FALSE, 1),
                ('d5d50002-0000-4000-8000-000000000004', 'd5d50002-0000-4000-8000-000000000001', '5', '25', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50002-0000-4000-8000-000000000005', 'd5d50002-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the sum of squares\n');
        END IF;

        -- Average of Array
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'average-of-array') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50003-0000-4000-8000-000000000001', 'Average of Array', 'average-of-array', 'Easy',
                'Given a line of space-separated integers, print their average formatted to exactly 2 decimal places.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50003-0000-4000-8000-000000000002', 'd5d50003-0000-4000-8000-000000000001', '1 2 3 4', '2.50', FALSE, 0),
                ('d5d50003-0000-4000-8000-000000000003', 'd5d50003-0000-4000-8000-000000000001', '5 5 5', '5.00', FALSE, 1),
                ('d5d50003-0000-4000-8000-000000000004', 'd5d50003-0000-4000-8000-000000000001', '10', '10.00', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50003-0000-4000-8000-000000000005', 'd5d50003-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the average with 2 decimal places\n');
        END IF;

        -- Check Even or Odd
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'check-even-or-odd') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50004-0000-4000-8000-000000000001', 'Check Even or Odd', 'check-even-or-odd', 'Easy',
                'Given an integer n, print "Even" or "Odd".',
                '-10^9 <= n <= 10^9', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50004-0000-4000-8000-000000000002', 'd5d50004-0000-4000-8000-000000000001', '4', 'Even', FALSE, 0),
                ('d5d50004-0000-4000-8000-000000000003', 'd5d50004-0000-4000-8000-000000000001', '7', 'Odd', FALSE, 1),
                ('d5d50004-0000-4000-8000-000000000004', 'd5d50004-0000-4000-8000-000000000001', '0', 'Even', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50004-0000-4000-8000-000000000005', 'd5d50004-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print Even or Odd\n');
        END IF;

        -- Multiplication Table
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'multiplication-table') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50005-0000-4000-8000-000000000001', 'Multiplication Table', 'multiplication-table', 'Easy',
                'Given an integer n, print n times 1 through n times 10, space-separated.',
                '1 <= n <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50005-0000-4000-8000-000000000002', 'd5d50005-0000-4000-8000-000000000001', '3', '3 6 9 12 15 18 21 24 27 30', FALSE, 0),
                ('d5d50005-0000-4000-8000-000000000003', 'd5d50005-0000-4000-8000-000000000001', '1', '1 2 3 4 5 6 7 8 9 10', FALSE, 1),
                ('d5d50005-0000-4000-8000-000000000004', 'd5d50005-0000-4000-8000-000000000001', '5', '5 10 15 20 25 30 35 40 45 50', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50005-0000-4000-8000-000000000005', 'd5d50005-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print n*1..n*10, space-separated\n');
        END IF;

        -- ASCII Value of Character
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'ascii-value-of-character') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50006-0000-4000-8000-000000000001', 'ASCII Value of Character', 'ascii-value-of-character', 'Easy',
                'Given a single character, print its ASCII value.',
                'input is exactly one printable ASCII character', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50006-0000-4000-8000-000000000002', 'd5d50006-0000-4000-8000-000000000001', 'A', '65', FALSE, 0),
                ('d5d50006-0000-4000-8000-000000000003', 'd5d50006-0000-4000-8000-000000000001', 'a', '97', FALSE, 1),
                ('d5d50006-0000-4000-8000-000000000004', 'd5d50006-0000-4000-8000-000000000001', '0', '48', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50006-0000-4000-8000-000000000005', 'd5d50006-0000-4000-8000-000000000001', 'PYTHON3',
                    'c = input()\n\n# TODO: print the ASCII value\n');
        END IF;

        -- Character from ASCII Value
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'character-from-ascii-value') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50007-0000-4000-8000-000000000001', 'Character from ASCII Value', 'character-from-ascii-value', 'Easy',
                'Given an ASCII code, print the corresponding character.',
                '32 <= n <= 126', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50007-0000-4000-8000-000000000002', 'd5d50007-0000-4000-8000-000000000001', '65', 'A', FALSE, 0),
                ('d5d50007-0000-4000-8000-000000000003', 'd5d50007-0000-4000-8000-000000000001', '97', 'a', FALSE, 1),
                ('d5d50007-0000-4000-8000-000000000004', 'd5d50007-0000-4000-8000-000000000001', '48', '0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50007-0000-4000-8000-000000000005', 'd5d50007-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print the character for this ASCII code\n');
        END IF;

        -- Vowel or Consonant
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'vowel-or-consonant') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50008-0000-4000-8000-000000000001', 'Vowel or Consonant', 'vowel-or-consonant', 'Easy',
                'Given a single lowercase letter, print "Vowel" or "Consonant".',
                'input is exactly one lowercase letter', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50008-0000-4000-8000-000000000002', 'd5d50008-0000-4000-8000-000000000001', 'a', 'Vowel', FALSE, 0),
                ('d5d50008-0000-4000-8000-000000000003', 'd5d50008-0000-4000-8000-000000000001', 'b', 'Consonant', FALSE, 1),
                ('d5d50008-0000-4000-8000-000000000004', 'd5d50008-0000-4000-8000-000000000001', 'z', 'Consonant', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50008-0000-4000-8000-000000000005', 'd5d50008-0000-4000-8000-000000000001', 'PYTHON3',
                    'c = input()\n\n# TODO: print Vowel or Consonant\n');
        END IF;

        -- Sum of First N Natural Numbers
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'sum-of-first-n-natural-numbers') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50009-0000-4000-8000-000000000001', 'Sum of First N Natural Numbers', 'sum-of-first-n-natural-numbers', 'Easy',
                'Given a positive integer n, print 1 + 2 + ... + n.',
                '1 <= n <= 10^9', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50009-0000-4000-8000-000000000002', 'd5d50009-0000-4000-8000-000000000001', '5', '15', FALSE, 0),
                ('d5d50009-0000-4000-8000-000000000003', 'd5d50009-0000-4000-8000-000000000001', '1', '1', FALSE, 1),
                ('d5d50009-0000-4000-8000-000000000004', 'd5d50009-0000-4000-8000-000000000001', '100', '5050', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50009-0000-4000-8000-000000000005', 'd5d50009-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print the sum 1..n\n');
        END IF;

        -- Digital Root
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'digital-root') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50010-0000-4000-8000-000000000001', 'Digital Root', 'digital-root', 'Easy',
                'Given a non-negative integer n, repeatedly sum its digits until a single digit remains, and print that digit.',
                '0 <= n <= 10^9', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50010-0000-4000-8000-000000000002', 'd5d50010-0000-4000-8000-000000000001', '9875', '2', FALSE, 0),
                ('d5d50010-0000-4000-8000-000000000003', 'd5d50010-0000-4000-8000-000000000001', '0', '0', FALSE, 1),
                ('d5d50010-0000-4000-8000-000000000004', 'd5d50010-0000-4000-8000-000000000001', '132', '6', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50010-0000-4000-8000-000000000005', 'd5d50010-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print the digital root\n');
        END IF;

        -- Trailing Zeros in Factorial
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'trailing-zeros-in-factorial') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50011-0000-4000-8000-000000000001', 'Trailing Zeros in Factorial', 'trailing-zeros-in-factorial', 'Easy',
                'Given a non-negative integer n, print the number of trailing zeros in n!.',
                '0 <= n <= 10^6', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50011-0000-4000-8000-000000000002', 'd5d50011-0000-4000-8000-000000000001', '5', '1', FALSE, 0),
                ('d5d50011-0000-4000-8000-000000000003', 'd5d50011-0000-4000-8000-000000000001', '10', '2', FALSE, 1),
                ('d5d50011-0000-4000-8000-000000000004', 'd5d50011-0000-4000-8000-000000000001', '25', '6', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50011-0000-4000-8000-000000000005', 'd5d50011-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print the number of trailing zeros in n!\n');
        END IF;

        -- Smallest Element in Array
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'smallest-element-in-array') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50012-0000-4000-8000-000000000001', 'Smallest Element in Array', 'smallest-element-in-array', 'Easy',
                'Given a line of space-separated integers, print the minimum value.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50012-0000-4000-8000-000000000002', 'd5d50012-0000-4000-8000-000000000001', '5 3 8 1', '1', FALSE, 0),
                ('d5d50012-0000-4000-8000-000000000003', 'd5d50012-0000-4000-8000-000000000001', '-2 -5 0', '-5', FALSE, 1),
                ('d5d50012-0000-4000-8000-000000000004', 'd5d50012-0000-4000-8000-000000000001', '7', '7', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50012-0000-4000-8000-000000000005', 'd5d50012-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the minimum value\n');
        END IF;

        -- Array Contains Value
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'array-contains-value') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50013-0000-4000-8000-000000000001', 'Array Contains Value', 'array-contains-value', 'Easy',
                'Given a line of space-separated integers and a second line with a target integer, print "true" if the target is in the array, otherwise "false".',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50013-0000-4000-8000-000000000002', 'd5d50013-0000-4000-8000-000000000001', '1 2 3\n2', 'true', FALSE, 0),
                ('d5d50013-0000-4000-8000-000000000003', 'd5d50013-0000-4000-8000-000000000001', '1 2 3\n5', 'false', FALSE, 1),
                ('d5d50013-0000-4000-8000-000000000004', 'd5d50013-0000-4000-8000-000000000001', '9\n9', 'true', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50013-0000-4000-8000-000000000005', 'd5d50013-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\ntarget = int(input())\n\n# TODO: print true or false\n');
        END IF;

        -- Concatenate Two Strings
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'concatenate-two-strings') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50014-0000-4000-8000-000000000001', 'Concatenate Two Strings', 'concatenate-two-strings', 'Easy',
                'Given two space-separated words on one line, print them concatenated together with no separator.',
                '1 <= length of each word <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50014-0000-4000-8000-000000000002', 'd5d50014-0000-4000-8000-000000000001', 'hello world', 'helloworld', FALSE, 0),
                ('d5d50014-0000-4000-8000-000000000003', 'd5d50014-0000-4000-8000-000000000001', 'foo bar', 'foobar', FALSE, 1),
                ('d5d50014-0000-4000-8000-000000000004', 'd5d50014-0000-4000-8000-000000000001', 'a b', 'ab', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50014-0000-4000-8000-000000000005', 'd5d50014-0000-4000-8000-000000000001', 'PYTHON3',
                    'a, b = input().split()\n\n# TODO: print a and b concatenated\n');
        END IF;

        -- String Length
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'string-length') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50015-0000-4000-8000-000000000001', 'String Length', 'string-length', 'Easy',
                'Given a line of text, print its length (including any spaces within it).',
                '0 <= length <= 10^4', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50015-0000-4000-8000-000000000002', 'd5d50015-0000-4000-8000-000000000001', 'hello', '5', FALSE, 0),
                ('d5d50015-0000-4000-8000-000000000003', 'd5d50015-0000-4000-8000-000000000001', 'a b c', '5', FALSE, 1),
                ('d5d50015-0000-4000-8000-000000000004', 'd5d50015-0000-4000-8000-000000000001', 'x', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50015-0000-4000-8000-000000000005', 'd5d50015-0000-4000-8000-000000000001', 'PYTHON3',
                    'text = input()\n\n# TODO: print the length of text\n');
        END IF;

        -- Check if String is a Rotation
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'check-string-rotation') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50016-0000-4000-8000-000000000001', 'Check String Rotation', 'check-string-rotation', 'Easy',
                'Given two space-separated words of equal length, print "true" if the second is a rotation of the first, otherwise "false".',
                '1 <= length of each word <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50016-0000-4000-8000-000000000002', 'd5d50016-0000-4000-8000-000000000001', 'waterbottle erbottlewat', 'true', FALSE, 0),
                ('d5d50016-0000-4000-8000-000000000003', 'd5d50016-0000-4000-8000-000000000001', 'abc acb', 'false', FALSE, 1),
                ('d5d50016-0000-4000-8000-000000000004', 'd5d50016-0000-4000-8000-000000000001', 'abcd abcd', 'true', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50016-0000-4000-8000-000000000005', 'd5d50016-0000-4000-8000-000000000001', 'PYTHON3',
                    'a, b = input().split()\n\n# TODO: print true if b is a rotation of a, else false\n');
        END IF;

        -- Longest Common Prefix
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'longest-common-prefix') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50017-0000-4000-8000-000000000001', 'Longest Common Prefix', 'longest-common-prefix', 'Medium',
                'Given a line of space-separated words, print the longest common prefix shared by all of them (an empty line if there is none).',
                '1 <= words <= 200', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50017-0000-4000-8000-000000000002', 'd5d50017-0000-4000-8000-000000000001', 'flower flow flight', 'fl', FALSE, 0),
                ('d5d50017-0000-4000-8000-000000000003', 'd5d50017-0000-4000-8000-000000000001', 'dog racecar car', '', FALSE, 1),
                ('d5d50017-0000-4000-8000-000000000004', 'd5d50017-0000-4000-8000-000000000001', 'interspecies interstellar interstate', 'inters', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50017-0000-4000-8000-000000000005', 'd5d50017-0000-4000-8000-000000000001', 'PYTHON3',
                    'words = input().split()\n\n# TODO: print the longest common prefix\n');
        END IF;

        -- Longest Palindromic Substring Length
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'longest-palindromic-substring-length') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50018-0000-4000-8000-000000000001', 'Longest Palindromic Substring Length', 'longest-palindromic-substring-length', 'Medium',
                'Given a lowercase string, print the length of its longest palindromic substring.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50018-0000-4000-8000-000000000002', 'd5d50018-0000-4000-8000-000000000001', 'babad', '3', FALSE, 0),
                ('d5d50018-0000-4000-8000-000000000003', 'd5d50018-0000-4000-8000-000000000001', 'cbbd', '2', FALSE, 1),
                ('d5d50018-0000-4000-8000-000000000004', 'd5d50018-0000-4000-8000-000000000001', 'a', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50018-0000-4000-8000-000000000005', 'd5d50018-0000-4000-8000-000000000001', 'PYTHON3',
                    's = input().strip()\n\n# TODO: print the length of the longest palindromic substring\n');
        END IF;

        -- Group Anagrams Count
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'group-anagrams-count') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50019-0000-4000-8000-000000000001', 'Group Anagrams Count', 'group-anagrams-count', 'Medium',
                'Given a line of space-separated lowercase words, print the number of distinct anagram groups.',
                '1 <= words <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50019-0000-4000-8000-000000000002', 'd5d50019-0000-4000-8000-000000000001', 'eat tea tan ate nat bat', '3', FALSE, 0),
                ('d5d50019-0000-4000-8000-000000000003', 'd5d50019-0000-4000-8000-000000000001', 'abc bca xyz', '2', FALSE, 1),
                ('d5d50019-0000-4000-8000-000000000004', 'd5d50019-0000-4000-8000-000000000001', 'a', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50019-0000-4000-8000-000000000005', 'd5d50019-0000-4000-8000-000000000001', 'PYTHON3',
                    'words = input().split()\n\n# TODO: print the number of distinct anagram groups\n');
        END IF;

        -- Subarray Sum Equals K
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'subarray-sum-equals-k') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50020-0000-4000-8000-000000000001', 'Subarray Sum Equals K', 'subarray-sum-equals-k', 'Medium',
                'Given a line of space-separated integers and a second line with an integer k, print the number of contiguous subarrays whose sum equals k.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50020-0000-4000-8000-000000000002', 'd5d50020-0000-4000-8000-000000000001', '1 1 1\n2', '2', FALSE, 0),
                ('d5d50020-0000-4000-8000-000000000003', 'd5d50020-0000-4000-8000-000000000001', '1 2 3\n3', '2', FALSE, 1),
                ('d5d50020-0000-4000-8000-000000000004', 'd5d50020-0000-4000-8000-000000000001', '1\n1', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50020-0000-4000-8000-000000000005', 'd5d50020-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\nk = int(input())\n\n# TODO: print the number of subarrays summing to k\n');
        END IF;

        -- Merge Intervals
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'merge-intervals') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50021-0000-4000-8000-000000000001', 'Merge Intervals', 'merge-intervals', 'Medium',
                'Given a line of space-separated integers representing intervals as consecutive (start, end) pairs sorted by start, merge all overlapping (or touching) intervals and print the result as flattened (start, end) pairs, space-separated.',
                '1 <= number of intervals <= 500', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50021-0000-4000-8000-000000000002', 'd5d50021-0000-4000-8000-000000000001', '1 3 2 6 8 10 15 18', '1 6 8 10 15 18', FALSE, 0),
                ('d5d50021-0000-4000-8000-000000000003', 'd5d50021-0000-4000-8000-000000000001', '1 4 4 5', '1 5', FALSE, 1),
                ('d5d50021-0000-4000-8000-000000000004', 'd5d50021-0000-4000-8000-000000000001', '1 4', '1 4', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50021-0000-4000-8000-000000000005', 'd5d50021-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\nintervals = [(nums[i], nums[i+1]) for i in range(0, len(nums), 2)]\n\n# TODO: merge overlapping intervals, print flattened start/end pairs\n');
        END IF;

        -- Rotate Matrix 90 Degrees Clockwise
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'rotate-matrix-90-degrees') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50022-0000-4000-8000-000000000001', 'Rotate Matrix 90 Degrees', 'rotate-matrix-90-degrees', 'Medium',
                'The first line contains an integer n. The next n lines each contain n space-separated integers (an n x n matrix). Print the matrix rotated 90 degrees clockwise, one row per line.',
                '1 <= n <= 100', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50022-0000-4000-8000-000000000002', 'd5d50022-0000-4000-8000-000000000001', '2\n1 2\n3 4', '3 1\n4 2', FALSE, 0),
                ('d5d50022-0000-4000-8000-000000000003', 'd5d50022-0000-4000-8000-000000000001', '3\n1 2 3\n4 5 6\n7 8 9', '7 4 1\n8 5 2\n9 6 3', FALSE, 1),
                ('d5d50022-0000-4000-8000-000000000004', 'd5d50022-0000-4000-8000-000000000001', '1\n5', '5', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50022-0000-4000-8000-000000000005', 'd5d50022-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\nmatrix = [list(map(int, input().split())) for _ in range(n)]\n\n# TODO: print the matrix rotated 90 degrees clockwise\n');
        END IF;

        -- Spiral Order Matrix
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'spiral-order-matrix') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50023-0000-4000-8000-000000000001', 'Spiral Order Matrix', 'spiral-order-matrix', 'Medium',
                'The first line contains an integer n. The next n lines each contain n space-separated integers (an n x n matrix). Print all elements in spiral (clockwise, starting top-left) order, space-separated.',
                '1 <= n <= 100', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50023-0000-4000-8000-000000000002', 'd5d50023-0000-4000-8000-000000000001', '3\n1 2 3\n4 5 6\n7 8 9', '1 2 3 6 9 8 7 4 5', FALSE, 0),
                ('d5d50023-0000-4000-8000-000000000003', 'd5d50023-0000-4000-8000-000000000001', '2\n1 2\n3 4', '1 2 4 3', FALSE, 1),
                ('d5d50023-0000-4000-8000-000000000004', 'd5d50023-0000-4000-8000-000000000001', '1\n7', '7', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50023-0000-4000-8000-000000000005', 'd5d50023-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\nmatrix = [list(map(int, input().split())) for _ in range(n)]\n\n# TODO: print all elements in spiral order\n');
        END IF;

        -- Word Break Check
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'word-break-check') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50024-0000-4000-8000-000000000001', 'Word Break Check', 'word-break-check', 'Medium',
                'The first line is a string s with no spaces. The second line is a space-separated dictionary of words. Print "true" if s can be segmented into a sequence of one or more dictionary words, otherwise "false".',
                '1 <= length of s <= 300', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50024-0000-4000-8000-000000000002', 'd5d50024-0000-4000-8000-000000000001', 'leetcode\nleet code', 'true', FALSE, 0),
                ('d5d50024-0000-4000-8000-000000000003', 'd5d50024-0000-4000-8000-000000000001', 'catsandog\ncats dog sand and cat', 'false', FALSE, 1),
                ('d5d50024-0000-4000-8000-000000000004', 'd5d50024-0000-4000-8000-000000000001', 'a\na', 'true', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50024-0000-4000-8000-000000000005', 'd5d50024-0000-4000-8000-000000000001', 'PYTHON3',
                    's = input().strip()\nwords = set(input().split())\n\n# TODO: print true or false\n');
        END IF;

        -- Coin Change Minimum Coins
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'coin-change-minimum-coins') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50025-0000-4000-8000-000000000001', 'Coin Change Minimum Coins', 'coin-change-minimum-coins', 'Medium',
                'Given a line of space-separated coin denominations and a second line with a target amount, print the minimum number of coins needed to make that amount, or -1 if it cannot be made (unlimited supply of each coin).',
                '1 <= amount <= 10^4', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50025-0000-4000-8000-000000000002', 'd5d50025-0000-4000-8000-000000000001', '1 2 5\n11', '3', FALSE, 0),
                ('d5d50025-0000-4000-8000-000000000003', 'd5d50025-0000-4000-8000-000000000001', '2\n3', '-1', FALSE, 1),
                ('d5d50025-0000-4000-8000-000000000004', 'd5d50025-0000-4000-8000-000000000001', '1\n0', '0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50025-0000-4000-8000-000000000005', 'd5d50025-0000-4000-8000-000000000001', 'PYTHON3',
                    'coins = list(map(int, input().split()))\namount = int(input())\n\n# TODO: print the minimum number of coins, or -1\n');
        END IF;

        -- House Robber Max
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'house-robber-max') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50026-0000-4000-8000-000000000001', 'House Robber Max', 'house-robber-max', 'Medium',
                'Given a line of space-separated non-negative integers representing money in houses arranged in a line, print the maximum amount you can rob without robbing two adjacent houses.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50026-0000-4000-8000-000000000002', 'd5d50026-0000-4000-8000-000000000001', '1 2 3 1', '4', FALSE, 0),
                ('d5d50026-0000-4000-8000-000000000003', 'd5d50026-0000-4000-8000-000000000001', '2 7 9 3 1', '12', FALSE, 1),
                ('d5d50026-0000-4000-8000-000000000004', 'd5d50026-0000-4000-8000-000000000001', '5', '5', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50026-0000-4000-8000-000000000005', 'd5d50026-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the max amount that can be robbed, no two adjacent houses\n');
        END IF;

        -- Climbing Stairs Ways
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'climbing-stairs-ways') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50027-0000-4000-8000-000000000001', 'Climbing Stairs Ways', 'climbing-stairs-ways', 'Medium',
                'Given n stairs, and being able to climb 1 or 2 steps at a time, print the number of distinct ways to reach the top.',
                '1 <= n <= 45', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50027-0000-4000-8000-000000000002', 'd5d50027-0000-4000-8000-000000000001', '2', '2', FALSE, 0),
                ('d5d50027-0000-4000-8000-000000000003', 'd5d50027-0000-4000-8000-000000000001', '3', '3', FALSE, 1),
                ('d5d50027-0000-4000-8000-000000000004', 'd5d50027-0000-4000-8000-000000000001', '5', '8', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50027-0000-4000-8000-000000000005', 'd5d50027-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print the number of distinct ways to climb n stairs (1 or 2 steps)\n');
        END IF;

        -- Unique Paths in Grid
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'unique-paths-in-grid') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50028-0000-4000-8000-000000000001', 'Unique Paths in Grid', 'unique-paths-in-grid', 'Medium',
                'Given two space-separated integers m and n on one line (grid dimensions), print the number of unique paths from the top-left to the bottom-right corner of an m x n grid, moving only right or down.',
                '1 <= m, n <= 100', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50028-0000-4000-8000-000000000002', 'd5d50028-0000-4000-8000-000000000001', '3 7', '28', FALSE, 0),
                ('d5d50028-0000-4000-8000-000000000003', 'd5d50028-0000-4000-8000-000000000001', '3 2', '3', FALSE, 1),
                ('d5d50028-0000-4000-8000-000000000004', 'd5d50028-0000-4000-8000-000000000001', '1 1', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50028-0000-4000-8000-000000000005', 'd5d50028-0000-4000-8000-000000000001', 'PYTHON3',
                    'm, n = map(int, input().split())\n\n# TODO: print the number of unique paths in an m x n grid\n');
        END IF;

        -- Longest Increasing Subsequence Length
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'longest-increasing-subsequence-length') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50029-0000-4000-8000-000000000001', 'Longest Increasing Subsequence Length', 'longest-increasing-subsequence-length', 'Medium',
                'Given a line of space-separated integers, print the length of the longest strictly increasing subsequence.',
                '1 <= length <= 2000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50029-0000-4000-8000-000000000002', 'd5d50029-0000-4000-8000-000000000001', '10 9 2 5 3 7 101 18', '4', FALSE, 0),
                ('d5d50029-0000-4000-8000-000000000003', 'd5d50029-0000-4000-8000-000000000001', '0 1 0 3 2 3', '4', FALSE, 1),
                ('d5d50029-0000-4000-8000-000000000004', 'd5d50029-0000-4000-8000-000000000001', '7 7 7 7', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50029-0000-4000-8000-000000000005', 'd5d50029-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the length of the longest strictly increasing subsequence\n');
        END IF;

        -- Number of Islands
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'number-of-islands') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50030-0000-4000-8000-000000000001', 'Number of Islands', 'number-of-islands', 'Medium',
                'The first line contains an integer rows. The next rows lines each contain a string of ''0''/''1'' characters (a grid, all rows the same length). Print the number of islands (connected groups of ''1''s, connected 4-directionally).',
                '1 <= rows <= 300', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50030-0000-4000-8000-000000000002', 'd5d50030-0000-4000-8000-000000000001', '4\n11000\n11000\n00100\n00011', '3', FALSE, 0),
                ('d5d50030-0000-4000-8000-000000000003', 'd5d50030-0000-4000-8000-000000000001', '2\n10\n01', '2', FALSE, 1),
                ('d5d50030-0000-4000-8000-000000000004', 'd5d50030-0000-4000-8000-000000000001', '1\n111', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50030-0000-4000-8000-000000000005', 'd5d50030-0000-4000-8000-000000000001', 'PYTHON3',
                    'rows = int(input())\ngrid = [input().strip() for _ in range(rows)]\n\n# TODO: print the number of islands\n');
        END IF;

        -- Course Schedule Possible
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'course-schedule-possible') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50031-0000-4000-8000-000000000001', 'Course Schedule Possible', 'course-schedule-possible', 'Medium',
                'The first line contains two integers n (number of courses) and m (number of prerequisite pairs). Each of the next m lines contains two integers "a b" meaning course b must be taken before course a. Print "true" if all courses can be finished (no cycle), otherwise "false".',
                '1 <= n <= 1000, 0 <= m <= 5000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50031-0000-4000-8000-000000000002', 'd5d50031-0000-4000-8000-000000000001', '2 1\n1 0', 'true', FALSE, 0),
                ('d5d50031-0000-4000-8000-000000000003', 'd5d50031-0000-4000-8000-000000000001', '2 2\n1 0\n0 1', 'false', FALSE, 1),
                ('d5d50031-0000-4000-8000-000000000004', 'd5d50031-0000-4000-8000-000000000001', '1 0', 'true', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50031-0000-4000-8000-000000000005', 'd5d50031-0000-4000-8000-000000000001', 'PYTHON3',
                    'n, m = map(int, input().split())\nedges = [tuple(map(int, input().split())) for _ in range(m)]\n\n# TODO: print true if all courses can be finished, else false\n');
        END IF;

        -- Kth Largest Element
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'kth-largest-element') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50032-0000-4000-8000-000000000001', 'Kth Largest Element', 'kth-largest-element', 'Medium',
                'Given a line of space-separated integers and a second line with an integer k, print the kth largest element (counting duplicates separately, i.e. the kth element in descending sorted order).',
                '1 <= k <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50032-0000-4000-8000-000000000002', 'd5d50032-0000-4000-8000-000000000001', '3 2 1 5 6 4\n2', '5', FALSE, 0),
                ('d5d50032-0000-4000-8000-000000000003', 'd5d50032-0000-4000-8000-000000000001', '3 2 3 1 2 4 5 5 6\n4', '4', FALSE, 1),
                ('d5d50032-0000-4000-8000-000000000004', 'd5d50032-0000-4000-8000-000000000001', '1\n1', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50032-0000-4000-8000-000000000005', 'd5d50032-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\nk = int(input())\n\n# TODO: print the kth largest element (descending order, duplicates count separately)\n');
        END IF;

        -- Top K Frequent Elements
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'top-k-frequent-elements') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50033-0000-4000-8000-000000000001', 'Top K Frequent Elements', 'top-k-frequent-elements', 'Medium',
                'Given a line of space-separated integers and a second line with an integer k, print the k most frequent elements, space-separated. Break ties by whichever value first reaches that frequency earliest in the input.',
                '1 <= k <= distinct values <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50033-0000-4000-8000-000000000002', 'd5d50033-0000-4000-8000-000000000001', '1 1 1 2 2 3\n2', '1 2', FALSE, 0),
                ('d5d50033-0000-4000-8000-000000000003', 'd5d50033-0000-4000-8000-000000000001', '1\n1', '1', FALSE, 1),
                ('d5d50033-0000-4000-8000-000000000004', 'd5d50033-0000-4000-8000-000000000001', '5 5 4 4 3\n1', '5', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50033-0000-4000-8000-000000000005', 'd5d50033-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\nk = int(input())\n\n# TODO: print the k most frequent elements, space-separated\n');
        END IF;

        -- Container With Most Water
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'container-with-most-water') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50034-0000-4000-8000-000000000001', 'Container With Most Water', 'container-with-most-water', 'Medium',
                'Given a line of space-separated non-negative integers representing vertical line heights, print the maximum area of water that can be contained between any two lines.',
                '2 <= length <= 10^5', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50034-0000-4000-8000-000000000002', 'd5d50034-0000-4000-8000-000000000001', '1 8 6 2 5 4 8 3 7', '49', FALSE, 0),
                ('d5d50034-0000-4000-8000-000000000003', 'd5d50034-0000-4000-8000-000000000001', '1 1', '1', FALSE, 1),
                ('d5d50034-0000-4000-8000-000000000004', 'd5d50034-0000-4000-8000-000000000001', '4 3', '3', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50034-0000-4000-8000-000000000005', 'd5d50034-0000-4000-8000-000000000001', 'PYTHON3',
                    'heights = list(map(int, input().split()))\n\n# TODO: print the maximum container area\n');
        END IF;

        -- Product of Array Except Self
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'product-of-array-except-self') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50035-0000-4000-8000-000000000001', 'Product of Array Except Self', 'product-of-array-except-self', 'Medium',
                'Given a line of space-separated integers, print, for each index, the product of all the other elements (not using division), space-separated.',
                '2 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50035-0000-4000-8000-000000000002', 'd5d50035-0000-4000-8000-000000000001', '1 2 3 4', '24 12 8 6', FALSE, 0),
                ('d5d50035-0000-4000-8000-000000000003', 'd5d50035-0000-4000-8000-000000000001', '2 3 4', '12 8 6', FALSE, 1),
                ('d5d50035-0000-4000-8000-000000000004', 'd5d50035-0000-4000-8000-000000000001', '0 0', '0 0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50035-0000-4000-8000-000000000005', 'd5d50035-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print product of all elements except self, for each index\n');
        END IF;

        -- Edit Distance
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'edit-distance') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50036-0000-4000-8000-000000000001', 'Edit Distance', 'edit-distance', 'Hard',
                'Given two space-separated lowercase words, print the minimum number of single-character insertions, deletions, or substitutions required to transform the first word into the second.',
                '0 <= length of each word <= 500', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50036-0000-4000-8000-000000000002', 'd5d50036-0000-4000-8000-000000000001', 'horse ros', '3', FALSE, 0),
                ('d5d50036-0000-4000-8000-000000000003', 'd5d50036-0000-4000-8000-000000000001', 'intention execution', '5', FALSE, 1),
                ('d5d50036-0000-4000-8000-000000000004', 'd5d50036-0000-4000-8000-000000000001', 'abc abc', '0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50036-0000-4000-8000-000000000005', 'd5d50036-0000-4000-8000-000000000001', 'PYTHON3',
                    'a, b = input().split()\n\n# TODO: print the edit (Levenshtein) distance between a and b\n');
        END IF;

        -- Longest Common Subsequence Length
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'longest-common-subsequence-length') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50037-0000-4000-8000-000000000001', 'Longest Common Subsequence Length', 'longest-common-subsequence-length', 'Hard',
                'Given two space-separated lowercase words, print the length of their longest common subsequence.',
                '0 <= length of each word <= 1000', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50037-0000-4000-8000-000000000002', 'd5d50037-0000-4000-8000-000000000001', 'abcde ace', '3', FALSE, 0),
                ('d5d50037-0000-4000-8000-000000000003', 'd5d50037-0000-4000-8000-000000000001', 'abc abc', '3', FALSE, 1),
                ('d5d50037-0000-4000-8000-000000000004', 'd5d50037-0000-4000-8000-000000000001', 'abc def', '0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50037-0000-4000-8000-000000000005', 'd5d50037-0000-4000-8000-000000000001', 'PYTHON3',
                    'a, b = input().split()\n\n# TODO: print the length of the longest common subsequence\n');
        END IF;

        -- Median of Two Sorted Arrays
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'median-of-two-sorted-arrays') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50038-0000-4000-8000-000000000001', 'Median of Two Sorted Arrays', 'median-of-two-sorted-arrays', 'Hard',
                'Given two lines, each a space-separated ascending-sorted list of integers, print the median of the combined data set, formatted with exactly one decimal place.',
                '1 <= combined length <= 2000', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50038-0000-4000-8000-000000000002', 'd5d50038-0000-4000-8000-000000000001', '1 3\n2', '2.0', FALSE, 0),
                ('d5d50038-0000-4000-8000-000000000003', 'd5d50038-0000-4000-8000-000000000001', '1 2\n3 4', '2.5', FALSE, 1),
                ('d5d50038-0000-4000-8000-000000000004', 'd5d50038-0000-4000-8000-000000000001', '5\n6', '5.5', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50038-0000-4000-8000-000000000005', 'd5d50038-0000-4000-8000-000000000001', 'PYTHON3',
                    'a = list(map(int, input().split()))\nb = list(map(int, input().split()))\n\n# TODO: print the median of the merged array, one decimal place\n');
        END IF;

        -- Trapping Rain Water
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'trapping-rain-water') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50039-0000-4000-8000-000000000001', 'Trapping Rain Water', 'trapping-rain-water', 'Hard',
                'Given a line of space-separated non-negative integers representing an elevation map, print the total units of water that can be trapped after raining.',
                '1 <= length <= 2*10^4', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50039-0000-4000-8000-000000000002', 'd5d50039-0000-4000-8000-000000000001', '0 1 0 2 1 0 1 3 2 1 2 1', '6', FALSE, 0),
                ('d5d50039-0000-4000-8000-000000000003', 'd5d50039-0000-4000-8000-000000000001', '4 2 0 3 2 5', '9', FALSE, 1),
                ('d5d50039-0000-4000-8000-000000000004', 'd5d50039-0000-4000-8000-000000000001', '1 1', '0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50039-0000-4000-8000-000000000005', 'd5d50039-0000-4000-8000-000000000001', 'PYTHON3',
                    'heights = list(map(int, input().split()))\n\n# TODO: print the total trapped water\n');
        END IF;

        -- N-Queens Count
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'n-queens-count') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50040-0000-4000-8000-000000000001', 'N-Queens Count', 'n-queens-count', 'Hard',
                'Given an integer n, print the number of distinct ways to place n queens on an n x n chessboard so that no two queens attack each other.',
                '1 <= n <= 9', 5000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50040-0000-4000-8000-000000000002', 'd5d50040-0000-4000-8000-000000000001', '4', '2', FALSE, 0),
                ('d5d50040-0000-4000-8000-000000000003', 'd5d50040-0000-4000-8000-000000000001', '1', '1', FALSE, 1),
                ('d5d50040-0000-4000-8000-000000000004', 'd5d50040-0000-4000-8000-000000000001', '5', '10', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50040-0000-4000-8000-000000000005', 'd5d50040-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print the number of distinct N-Queens solutions\n');
        END IF;

        -- Word Ladder Length
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'word-ladder-length') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50041-0000-4000-8000-000000000001', 'Word Ladder Length', 'word-ladder-length', 'Hard',
                'The first line is beginWord, the second is endWord, and the third is a space-separated word list. Each step may change exactly one letter, and every intermediate word must be in the word list. Print the number of words in the shortest transformation sequence from beginWord to endWord (inclusive), or 0 if no such sequence exists.',
                '1 <= word length <= 10, 1 <= word list size <= 5000', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50041-0000-4000-8000-000000000002', 'd5d50041-0000-4000-8000-000000000001', 'hit\ncog\nhot dot dog lot log cog', '5', FALSE, 0),
                ('d5d50041-0000-4000-8000-000000000003', 'd5d50041-0000-4000-8000-000000000001', 'hit\ncog\nhot dot dog lot log', '0', FALSE, 1),
                ('d5d50041-0000-4000-8000-000000000004', 'd5d50041-0000-4000-8000-000000000001', 'lot\nlog\nlot log', '2', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50041-0000-4000-8000-000000000005', 'd5d50041-0000-4000-8000-000000000001', 'PYTHON3',
                    'begin = input().strip()\nend = input().strip()\nword_list = set(input().split())\n\n# TODO: BFS shortest transformation length, or 0\n');
        END IF;

        -- Minimum Path Sum in Grid
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'minimum-path-sum-in-grid') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50042-0000-4000-8000-000000000001', 'Minimum Path Sum in Grid', 'minimum-path-sum-in-grid', 'Hard',
                'The first line contains two integers rows and cols. The next rows lines each contain cols space-separated non-negative integers (a grid). Print the minimum sum path from top-left to bottom-right, moving only right or down.',
                '1 <= rows, cols <= 200', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50042-0000-4000-8000-000000000002', 'd5d50042-0000-4000-8000-000000000001', '3 3\n1 3 1\n1 5 1\n4 2 1', '7', FALSE, 0),
                ('d5d50042-0000-4000-8000-000000000003', 'd5d50042-0000-4000-8000-000000000001', '2 3\n1 2 3\n4 5 6', '12', FALSE, 1),
                ('d5d50042-0000-4000-8000-000000000004', 'd5d50042-0000-4000-8000-000000000001', '1 1\n5', '5', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50042-0000-4000-8000-000000000005', 'd5d50042-0000-4000-8000-000000000001', 'PYTHON3',
                    'rows, cols = map(int, input().split())\ngrid = [list(map(int, input().split())) for _ in range(rows)]\n\n# TODO: print the minimum path sum from top-left to bottom-right\n');
        END IF;

        -- Longest Valid Parentheses
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'longest-valid-parentheses') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50043-0000-4000-8000-000000000001', 'Longest Valid Parentheses', 'longest-valid-parentheses', 'Hard',
                'Given a string containing only ''('' and '')'' characters, print the length of the longest valid (well-formed and properly nested) parentheses substring.',
                '1 <= length <= 3*10^4', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50043-0000-4000-8000-000000000002', 'd5d50043-0000-4000-8000-000000000001', '(()', '2', FALSE, 0),
                ('d5d50043-0000-4000-8000-000000000003', 'd5d50043-0000-4000-8000-000000000001', ')()())', '4', FALSE, 1),
                ('d5d50043-0000-4000-8000-000000000004', 'd5d50043-0000-4000-8000-000000000001', '(', '0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50043-0000-4000-8000-000000000005', 'd5d50043-0000-4000-8000-000000000001', 'PYTHON3',
                    's = input().strip()\n\n# TODO: print the length of the longest valid parentheses substring\n');
        END IF;

        -- Maximum Product Subarray
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'maximum-product-subarray') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50044-0000-4000-8000-000000000001', 'Maximum Product Subarray', 'maximum-product-subarray', 'Hard',
                'Given a line of space-separated integers, print the maximum product of a contiguous subarray.',
                '1 <= length <= 1000', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50044-0000-4000-8000-000000000002', 'd5d50044-0000-4000-8000-000000000001', '2 3 -2 4', '6', FALSE, 0),
                ('d5d50044-0000-4000-8000-000000000003', 'd5d50044-0000-4000-8000-000000000001', '-2 0 -1', '0', FALSE, 1),
                ('d5d50044-0000-4000-8000-000000000004', 'd5d50044-0000-4000-8000-000000000001', '-2', '-2', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50044-0000-4000-8000-000000000005', 'd5d50044-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the maximum product of a contiguous subarray\n');
        END IF;

        -- Regular Expression Matching
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'regular-expression-matching') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50045-0000-4000-8000-000000000001', 'Regular Expression Matching', 'regular-expression-matching', 'Hard',
                'The first line is a lowercase string s. The second line is a pattern p containing lowercase letters, ''.'' (matches any single character), and ''*'' (matches zero or more of the preceding element). Print "true" if p matches the entirety of s, otherwise "false".',
                '0 <= length of s <= 20, 0 <= length of p <= 30', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50045-0000-4000-8000-000000000002', 'd5d50045-0000-4000-8000-000000000001', 'aa\na', 'false', FALSE, 0),
                ('d5d50045-0000-4000-8000-000000000003', 'd5d50045-0000-4000-8000-000000000001', 'aa\na*', 'true', FALSE, 1),
                ('d5d50045-0000-4000-8000-000000000004', 'd5d50045-0000-4000-8000-000000000001', 'ab\n.*', 'true', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50045-0000-4000-8000-000000000005', 'd5d50045-0000-4000-8000-000000000001', 'PYTHON3',
                    's = input()\np = input()\n\n# TODO: print true if p matches all of s, else false\n');
        END IF;

        -- Sliding Window Maximum
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'sliding-window-maximum') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50046-0000-4000-8000-000000000001', 'Sliding Window Maximum', 'sliding-window-maximum', 'Hard',
                'Given a line of space-separated integers and a second line with a window size k, print the maximum of each contiguous window of size k as it slides from left to right, space-separated.',
                '1 <= k <= length <= 10^5', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50046-0000-4000-8000-000000000002', 'd5d50046-0000-4000-8000-000000000001', '1 3 -1 -3 5 3 6 7\n3', '3 3 5 5 6 7', FALSE, 0),
                ('d5d50046-0000-4000-8000-000000000003', 'd5d50046-0000-4000-8000-000000000001', '9 11\n2', '11', FALSE, 1),
                ('d5d50046-0000-4000-8000-000000000004', 'd5d50046-0000-4000-8000-000000000001', '1\n1', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50046-0000-4000-8000-000000000005', 'd5d50046-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\nk = int(input())\n\n# TODO: print the max of each sliding window of size k\n');
        END IF;

        -- Merge K Sorted Arrays
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'merge-k-sorted-arrays') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50047-0000-4000-8000-000000000001', 'Merge K Sorted Arrays', 'merge-k-sorted-arrays', 'Hard',
                'The first line contains an integer k. Each of the next k lines contains a space-separated ascending-sorted array. Print all elements merged into a single ascending-sorted list, space-separated.',
                '1 <= k <= 1000', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50047-0000-4000-8000-000000000002', 'd5d50047-0000-4000-8000-000000000001', '3\n1 4 5\n1 3 4\n2 6', '1 1 2 3 4 4 5 6', FALSE, 0),
                ('d5d50047-0000-4000-8000-000000000003', 'd5d50047-0000-4000-8000-000000000001', '2\n1\n2', '1 2', FALSE, 1),
                ('d5d50047-0000-4000-8000-000000000004', 'd5d50047-0000-4000-8000-000000000001', '1\n5', '5', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50047-0000-4000-8000-000000000005', 'd5d50047-0000-4000-8000-000000000001', 'PYTHON3',
                    'k = int(input())\narrays = [list(map(int, input().split())) for _ in range(k)]\n\n# TODO: print all elements merged, ascending sorted\n');
        END IF;

        -- Dijkstra Shortest Path
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'dijkstra-shortest-path') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50048-0000-4000-8000-000000000001', 'Dijkstra Shortest Path', 'dijkstra-shortest-path', 'Hard',
                'The first line contains two integers n (nodes, numbered 0..n-1) and m (edges). Each of the next m lines contains three integers "u v w": a directed edge from u to v with weight w. The last line contains the source node. Print the shortest distance from the source to every node 0..n-1, space-separated, using -1 for unreachable nodes.',
                '1 <= n <= 1000, 0 <= m <= 5000, 0 <= w <= 1000', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50048-0000-4000-8000-000000000002', 'd5d50048-0000-4000-8000-000000000001', '4 5\n0 1 4\n0 2 1\n2 1 2\n1 3 1\n2 3 5\n0', '0 3 1 4', FALSE, 0),
                ('d5d50048-0000-4000-8000-000000000003', 'd5d50048-0000-4000-8000-000000000001', '2 1\n0 1 5\n0', '0 5', FALSE, 1),
                ('d5d50048-0000-4000-8000-000000000004', 'd5d50048-0000-4000-8000-000000000001', '1 0\n0', '0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50048-0000-4000-8000-000000000005', 'd5d50048-0000-4000-8000-000000000001', 'PYTHON3',
                    'n, m = map(int, input().split())\nedges = [tuple(map(int, input().split())) for _ in range(m)]\nsource = int(input())\n\n# TODO: Dijkstra, print shortest distance to every node (-1 if unreachable)\n');
        END IF;

        -- 0/1 Knapsack Maximum Value
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'knapsack-maximum-value') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50049-0000-4000-8000-000000000001', '0/1 Knapsack Maximum Value', 'knapsack-maximum-value', 'Hard',
                'The first line is the knapsack capacity. The second line is the number of items n. The third line is n space-separated weights. The fourth line is n space-separated values. Print the maximum total value achievable without exceeding the capacity (each item may be taken at most once).',
                '0 <= capacity <= 1000, 1 <= n <= 500', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50049-0000-4000-8000-000000000002', 'd5d50049-0000-4000-8000-000000000001', '50\n3\n10 20 30\n60 100 120', '220', FALSE, 0),
                ('d5d50049-0000-4000-8000-000000000003', 'd5d50049-0000-4000-8000-000000000001', '10\n2\n5 4\n10 40', '50', FALSE, 1),
                ('d5d50049-0000-4000-8000-000000000004', 'd5d50049-0000-4000-8000-000000000001', '0\n1\n5\n10', '0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50049-0000-4000-8000-000000000005', 'd5d50049-0000-4000-8000-000000000001', 'PYTHON3',
                    'capacity = int(input())\nn = int(input())\nweights = list(map(int, input().split()))\nvalues = list(map(int, input().split()))\n\n# TODO: print the maximum knapsack value\n');
        END IF;

        -- Longest Palindromic Subsequence Length
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'longest-palindromic-subsequence-length') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('d5d50050-0000-4000-8000-000000000001', 'Longest Palindromic Subsequence Length', 'longest-palindromic-subsequence-length', 'Hard',
                'Given a lowercase string, print the length of its longest palindromic subsequence (not necessarily contiguous).',
                '1 <= length <= 1000', 3000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('d5d50050-0000-4000-8000-000000000002', 'd5d50050-0000-4000-8000-000000000001', 'bbbab', '4', FALSE, 0),
                ('d5d50050-0000-4000-8000-000000000003', 'd5d50050-0000-4000-8000-000000000001', 'cbbd', '2', FALSE, 1),
                ('d5d50050-0000-4000-8000-000000000004', 'd5d50050-0000-4000-8000-000000000001', 'a', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('d5d50050-0000-4000-8000-000000000005', 'd5d50050-0000-4000-8000-000000000001', 'PYTHON3',
                    's = input().strip()\n\n# TODO: print the length of the longest palindromic subsequence\n');
        END IF;

    END IF;
END$$
DELIMITER ;

CALL seed_hard_and_more_judge_problems_0084();
DROP PROCEDURE seed_hard_and_more_judge_problems_0084;
