-- ============================================================================
-- Dev/staging seed data: expands the judge problem catalog from 4 to 50
-- problems. Follows the same prod-skip / idempotent pattern as V0082.
-- NOTE: to keep this migration a manageable size, only PYTHON3 starter code
-- is included for this batch (V0082's original 4 problems have both Python3
-- and JavaScript). Add JavaScript (or other language) starter code for any
-- of these via the admin panel as needed.
-- ============================================================================
DELIMITER $$
CREATE PROCEDURE `seed_more_judge_problems_0083`()
BEGIN
    IF DATABASE() <> 'codepulse-prod' THEN

        -- Sum of Array
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'sum-of-array') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40001-0000-4000-8000-000000000001', 'Sum of Array', 'sum-of-array', 'Easy',
                'Given a line of space-separated integers, print their sum.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40001-0000-4000-8000-000000000002', 'c4c40001-0000-4000-8000-000000000001', '1 2 3 4 5', '15', FALSE, 0),
                ('c4c40001-0000-4000-8000-000000000003', 'c4c40001-0000-4000-8000-000000000001', '-1 -2 3', '0', FALSE, 1),
                ('c4c40001-0000-4000-8000-000000000004', 'c4c40001-0000-4000-8000-000000000001', '10', '10', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40001-0000-4000-8000-000000000005', 'c4c40001-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the sum\n');
        END IF;

        -- Maximum of Array
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'maximum-of-array') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40002-0000-4000-8000-000000000001', 'Maximum of Array', 'maximum-of-array', 'Easy',
                'Given a line of space-separated integers, print the maximum value.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40002-0000-4000-8000-000000000002', 'c4c40002-0000-4000-8000-000000000001', '3 7 2 9 4', '9', FALSE, 0),
                ('c4c40002-0000-4000-8000-000000000003', 'c4c40002-0000-4000-8000-000000000001', '-5 -1 -10', '-1', FALSE, 1),
                ('c4c40002-0000-4000-8000-000000000004', 'c4c40002-0000-4000-8000-000000000001', '5', '5', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40002-0000-4000-8000-000000000005', 'c4c40002-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the maximum\n');
        END IF;

        -- Count Vowels
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'count-vowels') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40003-0000-4000-8000-000000000001', 'Count Vowels', 'count-vowels', 'Easy',
                'Given a line of lowercase text, print the number of vowels (a, e, i, o, u) in it.',
                '1 <= length <= 1000, lowercase letters and spaces only', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40003-0000-4000-8000-000000000002', 'c4c40003-0000-4000-8000-000000000001', 'hello world', '3', FALSE, 0),
                ('c4c40003-0000-4000-8000-000000000003', 'c4c40003-0000-4000-8000-000000000001', 'programming', '3', FALSE, 1),
                ('c4c40003-0000-4000-8000-000000000004', 'c4c40003-0000-4000-8000-000000000001', 'aeiou', '5', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40003-0000-4000-8000-000000000005', 'c4c40003-0000-4000-8000-000000000001', 'PYTHON3',
                    'text = input()\n\n# TODO: count and print the number of vowels\n');
        END IF;

        -- Factorial
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'factorial') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40004-0000-4000-8000-000000000001', 'Factorial', 'factorial', 'Easy',
                'Given a non-negative integer n, print n! (n factorial).',
                '0 <= n <= 15', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40004-0000-4000-8000-000000000002', 'c4c40004-0000-4000-8000-000000000001', '5', '120', FALSE, 0),
                ('c4c40004-0000-4000-8000-000000000003', 'c4c40004-0000-4000-8000-000000000001', '0', '1', FALSE, 1),
                ('c4c40004-0000-4000-8000-000000000004', 'c4c40004-0000-4000-8000-000000000001', '7', '5040', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40004-0000-4000-8000-000000000005', 'c4c40004-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print n factorial\n');
        END IF;

        -- Nth Fibonacci Number
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'nth-fibonacci-number') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40005-0000-4000-8000-000000000001', 'Nth Fibonacci Number', 'nth-fibonacci-number', 'Easy',
                'Given n (0-indexed, F(0)=0, F(1)=1), print the nth Fibonacci number.',
                '0 <= n <= 40', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40005-0000-4000-8000-000000000002', 'c4c40005-0000-4000-8000-000000000001', '10', '55', FALSE, 0),
                ('c4c40005-0000-4000-8000-000000000003', 'c4c40005-0000-4000-8000-000000000001', '0', '0', FALSE, 1),
                ('c4c40005-0000-4000-8000-000000000004', 'c4c40005-0000-4000-8000-000000000001', '1', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40005-0000-4000-8000-000000000005', 'c4c40005-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print the nth Fibonacci number\n');
        END IF;

        -- Is Prime
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'is-prime') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40006-0000-4000-8000-000000000001', 'Is Prime', 'is-prime', 'Easy',
                'Given an integer n, print "true" if it is prime, otherwise "false".',
                '1 <= n <= 10^6', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40006-0000-4000-8000-000000000002', 'c4c40006-0000-4000-8000-000000000001', '7', 'true', FALSE, 0),
                ('c4c40006-0000-4000-8000-000000000003', 'c4c40006-0000-4000-8000-000000000001', '10', 'false', FALSE, 1),
                ('c4c40006-0000-4000-8000-000000000004', 'c4c40006-0000-4000-8000-000000000001', '2', 'true', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40006-0000-4000-8000-000000000005', 'c4c40006-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print true or false\n');
        END IF;

        -- Greatest Common Divisor
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'greatest-common-divisor') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40007-0000-4000-8000-000000000001', 'Greatest Common Divisor', 'greatest-common-divisor', 'Easy',
                'Given two space-separated integers a and b, print their GCD.',
                '0 <= a, b <= 10^9', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40007-0000-4000-8000-000000000002', 'c4c40007-0000-4000-8000-000000000001', '48 18', '6', FALSE, 0),
                ('c4c40007-0000-4000-8000-000000000003', 'c4c40007-0000-4000-8000-000000000001', '7 13', '1', FALSE, 1),
                ('c4c40007-0000-4000-8000-000000000004', 'c4c40007-0000-4000-8000-000000000001', '0 5', '5', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40007-0000-4000-8000-000000000005', 'c4c40007-0000-4000-8000-000000000001', 'PYTHON3',
                    'a, b = map(int, input().split())\n\n# TODO: print gcd(a, b)\n');
        END IF;

        -- Least Common Multiple
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'least-common-multiple') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40008-0000-4000-8000-000000000001', 'Least Common Multiple', 'least-common-multiple', 'Easy',
                'Given two space-separated positive integers a and b, print their LCM.',
                '1 <= a, b <= 10^4', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40008-0000-4000-8000-000000000002', 'c4c40008-0000-4000-8000-000000000001', '4 6', '12', FALSE, 0),
                ('c4c40008-0000-4000-8000-000000000003', 'c4c40008-0000-4000-8000-000000000001', '3 7', '21', FALSE, 1),
                ('c4c40008-0000-4000-8000-000000000004', 'c4c40008-0000-4000-8000-000000000001', '5 5', '5', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40008-0000-4000-8000-000000000005', 'c4c40008-0000-4000-8000-000000000001', 'PYTHON3',
                    'a, b = map(int, input().split())\n\n# TODO: print lcm(a, b)\n');
        END IF;

        -- Reverse an Integer
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'reverse-an-integer') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40009-0000-4000-8000-000000000001', 'Reverse an Integer', 'reverse-an-integer', 'Easy',
                'Given an integer n, print it with its digits reversed. If n is negative, keep the minus sign. Drop any leading zeros produced by the reversal.',
                '-10^9 <= n <= 10^9', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40009-0000-4000-8000-000000000002', 'c4c40009-0000-4000-8000-000000000001', '12345', '54321', FALSE, 0),
                ('c4c40009-0000-4000-8000-000000000003', 'c4c40009-0000-4000-8000-000000000001', '-123', '-321', FALSE, 1),
                ('c4c40009-0000-4000-8000-000000000004', 'c4c40009-0000-4000-8000-000000000001', '100', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40009-0000-4000-8000-000000000005', 'c4c40009-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print n with digits reversed\n');
        END IF;

        -- Sum of Digits
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'sum-of-digits') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40010-0000-4000-8000-000000000001', 'Sum of Digits', 'sum-of-digits', 'Easy',
                'Given a non-negative integer n, print the sum of its digits.',
                '0 <= n <= 10^9', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40010-0000-4000-8000-000000000002', 'c4c40010-0000-4000-8000-000000000001', '12345', '15', FALSE, 0),
                ('c4c40010-0000-4000-8000-000000000003', 'c4c40010-0000-4000-8000-000000000001', '0', '0', FALSE, 1),
                ('c4c40010-0000-4000-8000-000000000004', 'c4c40010-0000-4000-8000-000000000001', '1001', '2', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40010-0000-4000-8000-000000000005', 'c4c40010-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print the sum of the digits of n\n');
        END IF;

        -- Count Digits
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'count-digits') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40011-0000-4000-8000-000000000001', 'Count Digits', 'count-digits', 'Easy',
                'Given a non-negative integer n, print how many digits it has.',
                '0 <= n <= 10^9', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40011-0000-4000-8000-000000000002', 'c4c40011-0000-4000-8000-000000000001', '12345', '5', FALSE, 0),
                ('c4c40011-0000-4000-8000-000000000003', 'c4c40011-0000-4000-8000-000000000001', '0', '1', FALSE, 1),
                ('c4c40011-0000-4000-8000-000000000004', 'c4c40011-0000-4000-8000-000000000001', '100000', '6', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40011-0000-4000-8000-000000000005', 'c4c40011-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = input().strip()\n\n# TODO: print the number of digits\n');
        END IF;

        -- Power of Two Check
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'power-of-two-check') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40012-0000-4000-8000-000000000001', 'Power of Two Check', 'power-of-two-check', 'Easy',
                'Given a positive integer n, print "true" if it is a power of two, otherwise "false".',
                '1 <= n <= 10^9', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40012-0000-4000-8000-000000000002', 'c4c40012-0000-4000-8000-000000000001', '16', 'true', FALSE, 0),
                ('c4c40012-0000-4000-8000-000000000003', 'c4c40012-0000-4000-8000-000000000001', '18', 'false', FALSE, 1),
                ('c4c40012-0000-4000-8000-000000000004', 'c4c40012-0000-4000-8000-000000000001', '1', 'true', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40012-0000-4000-8000-000000000005', 'c4c40012-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print true or false\n');
        END IF;

        -- Armstrong Number Check
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'armstrong-number-check') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40013-0000-4000-8000-000000000001', 'Armstrong Number Check', 'armstrong-number-check', 'Medium',
                'An Armstrong number equals the sum of its own digits, each raised to the power of the digit count. Given n, print "true" if it is an Armstrong number, otherwise "false".',
                '1 <= n <= 10^6', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40013-0000-4000-8000-000000000002', 'c4c40013-0000-4000-8000-000000000001', '153', 'true', FALSE, 0),
                ('c4c40013-0000-4000-8000-000000000003', 'c4c40013-0000-4000-8000-000000000001', '123', 'false', FALSE, 1),
                ('c4c40013-0000-4000-8000-000000000004', 'c4c40013-0000-4000-8000-000000000001', '9474', 'true', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40013-0000-4000-8000-000000000005', 'c4c40013-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print true or false\n');
        END IF;

        -- Binary to Decimal
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'binary-to-decimal') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40014-0000-4000-8000-000000000001', 'Binary to Decimal', 'binary-to-decimal', 'Easy',
                'Given a binary string, print its decimal value.',
                '1 <= length <= 32', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40014-0000-4000-8000-000000000002', 'c4c40014-0000-4000-8000-000000000001', '1010', '10', FALSE, 0),
                ('c4c40014-0000-4000-8000-000000000003', 'c4c40014-0000-4000-8000-000000000001', '1111', '15', FALSE, 1),
                ('c4c40014-0000-4000-8000-000000000004', 'c4c40014-0000-4000-8000-000000000001', '0', '0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40014-0000-4000-8000-000000000005', 'c4c40014-0000-4000-8000-000000000001', 'PYTHON3',
                    'binary = input().strip()\n\n# TODO: print the decimal value\n');
        END IF;

        -- Decimal to Binary
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'decimal-to-binary') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40015-0000-4000-8000-000000000001', 'Decimal to Binary', 'decimal-to-binary', 'Easy',
                'Given a non-negative integer n, print its binary representation (no leading zeros, except n=0 which prints "0").',
                '0 <= n <= 10^9', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40015-0000-4000-8000-000000000002', 'c4c40015-0000-4000-8000-000000000001', '10', '1010', FALSE, 0),
                ('c4c40015-0000-4000-8000-000000000003', 'c4c40015-0000-4000-8000-000000000001', '0', '0', FALSE, 1),
                ('c4c40015-0000-4000-8000-000000000004', 'c4c40015-0000-4000-8000-000000000001', '255', '11111111', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40015-0000-4000-8000-000000000005', 'c4c40015-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print the binary representation\n');
        END IF;

        -- Anagram Check
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'anagram-check') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40016-0000-4000-8000-000000000001', 'Anagram Check', 'anagram-check', 'Easy',
                'Given two space-separated lowercase words on one line, print "true" if they are anagrams of each other, otherwise "false".',
                '1 <= length of each word <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40016-0000-4000-8000-000000000002', 'c4c40016-0000-4000-8000-000000000001', 'listen silent', 'true', FALSE, 0),
                ('c4c40016-0000-4000-8000-000000000003', 'c4c40016-0000-4000-8000-000000000001', 'hello world', 'false', FALSE, 1),
                ('c4c40016-0000-4000-8000-000000000004', 'c4c40016-0000-4000-8000-000000000001', 'abc cab', 'true', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40016-0000-4000-8000-000000000005', 'c4c40016-0000-4000-8000-000000000001', 'PYTHON3',
                    'a, b = input().split()\n\n# TODO: print true or false\n');
        END IF;

        -- Count Distinct Elements
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'count-distinct-elements') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40017-0000-4000-8000-000000000001', 'Count Distinct Elements', 'count-distinct-elements', 'Easy',
                'Given a line of space-separated integers, print the number of distinct values.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40017-0000-4000-8000-000000000002', 'c4c40017-0000-4000-8000-000000000001', '1 2 2 3 3 3', '3', FALSE, 0),
                ('c4c40017-0000-4000-8000-000000000003', 'c4c40017-0000-4000-8000-000000000001', '5 5 5', '1', FALSE, 1),
                ('c4c40017-0000-4000-8000-000000000004', 'c4c40017-0000-4000-8000-000000000001', '1 2 3 4', '4', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40017-0000-4000-8000-000000000005', 'c4c40017-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the number of distinct values\n');
        END IF;

        -- Find Missing Number
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'find-missing-number') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40018-0000-4000-8000-000000000001', 'Find Missing Number', 'find-missing-number', 'Easy',
                'Given a line of space-separated distinct integers containing all but one of the numbers from 1 to n (where n is one more than the count of numbers given), print the missing number.',
                '1 <= n <= 10^5', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40018-0000-4000-8000-000000000002', 'c4c40018-0000-4000-8000-000000000001', '1 2 4 5', '3', FALSE, 0),
                ('c4c40018-0000-4000-8000-000000000003', 'c4c40018-0000-4000-8000-000000000001', '2 3 4 5', '1', FALSE, 1),
                ('c4c40018-0000-4000-8000-000000000004', 'c4c40018-0000-4000-8000-000000000001', '1 3', '2', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40018-0000-4000-8000-000000000005', 'c4c40018-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the missing number from 1..len(nums)+1\n');
        END IF;

        -- Second Largest Distinct Element
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'second-largest-distinct-element') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40019-0000-4000-8000-000000000001', 'Second Largest Distinct Element', 'second-largest-distinct-element', 'Easy',
                'Given a line of space-separated integers with at least two distinct values, print the second largest distinct value.',
                '2 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40019-0000-4000-8000-000000000002', 'c4c40019-0000-4000-8000-000000000001', '10 20 4 45 99', '45', FALSE, 0),
                ('c4c40019-0000-4000-8000-000000000003', 'c4c40019-0000-4000-8000-000000000001', '3 3 4', '3', FALSE, 1),
                ('c4c40019-0000-4000-8000-000000000004', 'c4c40019-0000-4000-8000-000000000001', '1 2', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40019-0000-4000-8000-000000000005', 'c4c40019-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the second largest distinct value\n');
        END IF;

        -- Bubble Sort Ascending
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'bubble-sort-ascending') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40020-0000-4000-8000-000000000001', 'Bubble Sort Ascending', 'bubble-sort-ascending', 'Easy',
                'Given a line of space-separated integers, print them sorted in ascending order, space-separated.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40020-0000-4000-8000-000000000002', 'c4c40020-0000-4000-8000-000000000001', '5 3 1 4 2', '1 2 3 4 5', FALSE, 0),
                ('c4c40020-0000-4000-8000-000000000003', 'c4c40020-0000-4000-8000-000000000001', '9 8 7', '7 8 9', FALSE, 1),
                ('c4c40020-0000-4000-8000-000000000004', 'c4c40020-0000-4000-8000-000000000001', '1', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40020-0000-4000-8000-000000000005', 'c4c40020-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print sorted ascending, space-separated\n');
        END IF;

        -- Linear Search
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'linear-search') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40021-0000-4000-8000-000000000001', 'Linear Search', 'linear-search', 'Easy',
                'Given a line of space-separated integers and a second line with a target integer, print the 0-indexed position of the target, or -1 if not found.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40021-0000-4000-8000-000000000002', 'c4c40021-0000-4000-8000-000000000001', '4 2 7 1\n7', '2', FALSE, 0),
                ('c4c40021-0000-4000-8000-000000000003', 'c4c40021-0000-4000-8000-000000000001', '1 2 3\n5', '-1', FALSE, 1),
                ('c4c40021-0000-4000-8000-000000000004', 'c4c40021-0000-4000-8000-000000000001', '9\n9', '0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40021-0000-4000-8000-000000000005', 'c4c40021-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\ntarget = int(input())\n\n# TODO: print the index of target, or -1\n');
        END IF;

        -- Binary Search
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'binary-search') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40022-0000-4000-8000-000000000001', 'Binary Search', 'binary-search', 'Easy',
                'Given a line of space-separated, ascending-sorted integers and a second line with a target integer, print the 0-indexed position of the target, or -1 if not found.',
                '1 <= length <= 10^5', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40022-0000-4000-8000-000000000002', 'c4c40022-0000-4000-8000-000000000001', '1 3 5 7 9\n7', '3', FALSE, 0),
                ('c4c40022-0000-4000-8000-000000000003', 'c4c40022-0000-4000-8000-000000000001', '2 4 6\n5', '-1', FALSE, 1),
                ('c4c40022-0000-4000-8000-000000000004', 'c4c40022-0000-4000-8000-000000000001', '1\n1', '0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40022-0000-4000-8000-000000000005', 'c4c40022-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\ntarget = int(input())\n\n# TODO: binary search for target, print index or -1\n');
        END IF;

        -- Merge Two Sorted Arrays
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'merge-two-sorted-arrays') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40023-0000-4000-8000-000000000001', 'Merge Two Sorted Arrays', 'merge-two-sorted-arrays', 'Easy',
                'Given two lines, each a space-separated ascending-sorted list of integers, print the merged ascending-sorted list, space-separated.',
                '1 <= length of each <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40023-0000-4000-8000-000000000002', 'c4c40023-0000-4000-8000-000000000001', '1 3 5\n2 4 6', '1 2 3 4 5 6', FALSE, 0),
                ('c4c40023-0000-4000-8000-000000000003', 'c4c40023-0000-4000-8000-000000000001', '1 2\n3 4', '1 2 3 4', FALSE, 1),
                ('c4c40023-0000-4000-8000-000000000004', 'c4c40023-0000-4000-8000-000000000001', '5\n1', '1 5', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40023-0000-4000-8000-000000000005', 'c4c40023-0000-4000-8000-000000000001', 'PYTHON3',
                    'a = list(map(int, input().split()))\nb = list(map(int, input().split()))\n\n# TODO: print the merged sorted list, space-separated\n');
        END IF;

        -- Rotate Array Right by K
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'rotate-array-right-by-k') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40024-0000-4000-8000-000000000001', 'Rotate Array Right by K', 'rotate-array-right-by-k', 'Easy',
                'Given a line of space-separated integers and a second line with an integer k, print the array rotated right by k positions.',
                '1 <= length <= 1000, 0 <= k <= length', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40024-0000-4000-8000-000000000002', 'c4c40024-0000-4000-8000-000000000001', '1 2 3 4 5\n2', '4 5 1 2 3', FALSE, 0),
                ('c4c40024-0000-4000-8000-000000000003', 'c4c40024-0000-4000-8000-000000000001', '1 2 3\n1', '3 1 2', FALSE, 1),
                ('c4c40024-0000-4000-8000-000000000004', 'c4c40024-0000-4000-8000-000000000001', '1 2 3 4\n0', '1 2 3 4', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40024-0000-4000-8000-000000000005', 'c4c40024-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\nk = int(input())\n\n# TODO: print nums rotated right by k\n');
        END IF;

        -- Maximum Subarray Sum
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'maximum-subarray-sum') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40025-0000-4000-8000-000000000001', 'Maximum Subarray Sum', 'maximum-subarray-sum', 'Medium',
                'Given a line of space-separated integers, print the maximum possible sum of a contiguous subarray.',
                '1 <= length <= 10^5', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40025-0000-4000-8000-000000000002', 'c4c40025-0000-4000-8000-000000000001', '-2 1 -3 4 -1 2 1 -5 4', '6', FALSE, 0),
                ('c4c40025-0000-4000-8000-000000000003', 'c4c40025-0000-4000-8000-000000000001', '1 2 3', '6', FALSE, 1),
                ('c4c40025-0000-4000-8000-000000000004', 'c4c40025-0000-4000-8000-000000000001', '-1 -2', '-1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40025-0000-4000-8000-000000000005', 'c4c40025-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: Kadane\'s algorithm, print the max subarray sum\n');
        END IF;

        -- Valid Parentheses
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'valid-parentheses') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40026-0000-4000-8000-000000000001', 'Valid Parentheses', 'valid-parentheses', 'Easy',
                'Given a string containing only (){}[] characters, print "true" if the brackets are balanced and correctly nested, otherwise "false".',
                '1 <= length <= 10^4', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40026-0000-4000-8000-000000000002', 'c4c40026-0000-4000-8000-000000000001', '()[]{}', 'true', FALSE, 0),
                ('c4c40026-0000-4000-8000-000000000003', 'c4c40026-0000-4000-8000-000000000001', '(]', 'false', FALSE, 1),
                ('c4c40026-0000-4000-8000-000000000004', 'c4c40026-0000-4000-8000-000000000001', '()', 'true', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40026-0000-4000-8000-000000000005', 'c4c40026-0000-4000-8000-000000000001', 'PYTHON3',
                    's = input().strip()\n\n# TODO: print true or false\n');
        END IF;

        -- Count Words in a Sentence
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'count-words-in-a-sentence') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40027-0000-4000-8000-000000000001', 'Count Words in a Sentence', 'count-words-in-a-sentence', 'Easy',
                'Given a line of text, print the number of whitespace-separated words in it.',
                '1 <= length <= 10^4', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40027-0000-4000-8000-000000000002', 'c4c40027-0000-4000-8000-000000000001', 'the quick brown fox', '4', FALSE, 0),
                ('c4c40027-0000-4000-8000-000000000003', 'c4c40027-0000-4000-8000-000000000001', 'hello', '1', FALSE, 1),
                ('c4c40027-0000-4000-8000-000000000004', 'c4c40027-0000-4000-8000-000000000001', 'a b c d e', '5', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40027-0000-4000-8000-000000000005', 'c4c40027-0000-4000-8000-000000000001', 'PYTHON3',
                    'text = input()\n\n# TODO: print the number of words\n');
        END IF;

        -- Capitalize First Letter of Each Word
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'capitalize-first-letter-of-each-word') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40028-0000-4000-8000-000000000001', 'Capitalize First Letter of Each Word', 'capitalize-first-letter-of-each-word', 'Easy',
                'Given a line of lowercase, space-separated words, print the sentence with the first letter of each word capitalized.',
                '1 <= length <= 10^4', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40028-0000-4000-8000-000000000002', 'c4c40028-0000-4000-8000-000000000001', 'hello world', 'Hello World', FALSE, 0),
                ('c4c40028-0000-4000-8000-000000000003', 'c4c40028-0000-4000-8000-000000000001', 'the sky is blue', 'The Sky Is Blue', FALSE, 1),
                ('c4c40028-0000-4000-8000-000000000004', 'c4c40028-0000-4000-8000-000000000001', 'a', 'A', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40028-0000-4000-8000-000000000005', 'c4c40028-0000-4000-8000-000000000001', 'PYTHON3',
                    'text = input()\n\n# TODO: print with each word capitalized\n');
        END IF;

        -- Run-Length Encoding
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'run-length-encoding') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40029-0000-4000-8000-000000000001', 'Run-Length Encoding', 'run-length-encoding', 'Easy',
                'Given a lowercase string, print its run-length encoding: each character followed by the count of consecutive occurrences (e.g. "aaabbc" -> "a3b2c1").',
                '1 <= length <= 10^4', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40029-0000-4000-8000-000000000002', 'c4c40029-0000-4000-8000-000000000001', 'aaabbc', 'a3b2c1', FALSE, 0),
                ('c4c40029-0000-4000-8000-000000000003', 'c4c40029-0000-4000-8000-000000000001', 'abc', 'a1b1c1', FALSE, 1),
                ('c4c40029-0000-4000-8000-000000000004', 'c4c40029-0000-4000-8000-000000000001', 'aaaa', 'a4', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40029-0000-4000-8000-000000000005', 'c4c40029-0000-4000-8000-000000000001', 'PYTHON3',
                    's = input().strip()\n\n# TODO: print the run-length encoding\n');
        END IF;

        -- Longest Word in a Sentence
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'longest-word-in-a-sentence') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40030-0000-4000-8000-000000000001', 'Longest Word in a Sentence', 'longest-word-in-a-sentence', 'Easy',
                'Given a line of space-separated words, print the longest word. If there is a tie, print the first one that appears.',
                '1 <= length <= 10^4', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40030-0000-4000-8000-000000000002', 'c4c40030-0000-4000-8000-000000000001', 'I love programming languages', 'programming', FALSE, 0),
                ('c4c40030-0000-4000-8000-000000000003', 'c4c40030-0000-4000-8000-000000000001', 'a bb ccc', 'ccc', FALSE, 1),
                ('c4c40030-0000-4000-8000-000000000004', 'c4c40030-0000-4000-8000-000000000001', 'x', 'x', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40030-0000-4000-8000-000000000005', 'c4c40030-0000-4000-8000-000000000001', 'PYTHON3',
                    'words = input().split()\n\n# TODO: print the longest word (first one on ties)\n');
        END IF;

        -- Sort Array Descending
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'sort-array-descending') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40031-0000-4000-8000-000000000001', 'Sort Array Descending', 'sort-array-descending', 'Easy',
                'Given a line of space-separated integers, print them sorted in descending order, space-separated.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40031-0000-4000-8000-000000000002', 'c4c40031-0000-4000-8000-000000000001', '5 3 1 4 2', '5 4 3 2 1', FALSE, 0),
                ('c4c40031-0000-4000-8000-000000000003', 'c4c40031-0000-4000-8000-000000000001', '1 2 3', '3 2 1', FALSE, 1),
                ('c4c40031-0000-4000-8000-000000000004', 'c4c40031-0000-4000-8000-000000000001', '1', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40031-0000-4000-8000-000000000005', 'c4c40031-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print sorted descending, space-separated\n');
        END IF;

        -- Sum of Even Numbers
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'sum-of-even-numbers') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40032-0000-4000-8000-000000000001', 'Sum of Even Numbers', 'sum-of-even-numbers', 'Easy',
                'Given a line of space-separated integers, print the sum of the even ones.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40032-0000-4000-8000-000000000002', 'c4c40032-0000-4000-8000-000000000001', '1 2 3 4 5 6', '12', FALSE, 0),
                ('c4c40032-0000-4000-8000-000000000003', 'c4c40032-0000-4000-8000-000000000001', '1 3 5', '0', FALSE, 1),
                ('c4c40032-0000-4000-8000-000000000004', 'c4c40032-0000-4000-8000-000000000001', '2 4', '6', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40032-0000-4000-8000-000000000005', 'c4c40032-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the sum of even numbers\n');
        END IF;

        -- Sum of Odd Numbers
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'sum-of-odd-numbers') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40033-0000-4000-8000-000000000001', 'Sum of Odd Numbers', 'sum-of-odd-numbers', 'Easy',
                'Given a line of space-separated integers, print the sum of the odd ones.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40033-0000-4000-8000-000000000002', 'c4c40033-0000-4000-8000-000000000001', '1 2 3 4 5 6', '9', FALSE, 0),
                ('c4c40033-0000-4000-8000-000000000003', 'c4c40033-0000-4000-8000-000000000001', '2 4 6', '0', FALSE, 1),
                ('c4c40033-0000-4000-8000-000000000004', 'c4c40033-0000-4000-8000-000000000001', '1 3', '4', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40033-0000-4000-8000-000000000005', 'c4c40033-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the sum of odd numbers\n');
        END IF;

        -- Check Leap Year
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'check-leap-year') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40034-0000-4000-8000-000000000001', 'Check Leap Year', 'check-leap-year', 'Easy',
                'Given a year, print "true" if it is a leap year, otherwise "false".',
                '1 <= year <= 9999', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40034-0000-4000-8000-000000000002', 'c4c40034-0000-4000-8000-000000000001', '2020', 'true', FALSE, 0),
                ('c4c40034-0000-4000-8000-000000000003', 'c4c40034-0000-4000-8000-000000000001', '2021', 'false', FALSE, 1),
                ('c4c40034-0000-4000-8000-000000000004', 'c4c40034-0000-4000-8000-000000000001', '2000', 'true', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40034-0000-4000-8000-000000000005', 'c4c40034-0000-4000-8000-000000000001', 'PYTHON3',
                    'year = int(input())\n\n# TODO: print true or false\n');
        END IF;

        -- Celsius to Fahrenheit
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'celsius-to-fahrenheit') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40035-0000-4000-8000-000000000001', 'Celsius to Fahrenheit', 'celsius-to-fahrenheit', 'Easy',
                'Given a temperature in Celsius, print the equivalent in Fahrenheit with exactly one decimal place (F = C * 9/5 + 32).',
                '-273 <= C <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40035-0000-4000-8000-000000000002', 'c4c40035-0000-4000-8000-000000000001', '0', '32.0', FALSE, 0),
                ('c4c40035-0000-4000-8000-000000000003', 'c4c40035-0000-4000-8000-000000000001', '100', '212.0', FALSE, 1),
                ('c4c40035-0000-4000-8000-000000000004', 'c4c40035-0000-4000-8000-000000000001', '37', '98.6', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40035-0000-4000-8000-000000000005', 'c4c40035-0000-4000-8000-000000000001', 'PYTHON3',
                    'c = float(input())\n\n# TODO: print Fahrenheit with one decimal place\n');
        END IF;

        -- Count Set Bits
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'count-set-bits') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40036-0000-4000-8000-000000000001', 'Count Set Bits', 'count-set-bits', 'Easy',
                'Given a non-negative integer n, print the number of 1 bits in its binary representation.',
                '0 <= n <= 10^9', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40036-0000-4000-8000-000000000002', 'c4c40036-0000-4000-8000-000000000001', '7', '3', FALSE, 0),
                ('c4c40036-0000-4000-8000-000000000003', 'c4c40036-0000-4000-8000-000000000001', '8', '1', FALSE, 1),
                ('c4c40036-0000-4000-8000-000000000004', 'c4c40036-0000-4000-8000-000000000001', '0', '0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40036-0000-4000-8000-000000000005', 'c4c40036-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print the number of set bits\n');
        END IF;

        -- Swap Two Numbers
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'swap-two-numbers') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40037-0000-4000-8000-000000000001', 'Swap Two Numbers', 'swap-two-numbers', 'Easy',
                'Given two space-separated integers a and b, print them swapped (b then a), space-separated.',
                '-10^9 <= a, b <= 10^9', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40037-0000-4000-8000-000000000002', 'c4c40037-0000-4000-8000-000000000001', '3 5', '5 3', FALSE, 0),
                ('c4c40037-0000-4000-8000-000000000003', 'c4c40037-0000-4000-8000-000000000001', '1 1', '1 1', FALSE, 1),
                ('c4c40037-0000-4000-8000-000000000004', 'c4c40037-0000-4000-8000-000000000001', '0 9', '9 0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40037-0000-4000-8000-000000000005', 'c4c40037-0000-4000-8000-000000000001', 'PYTHON3',
                    'a, b = map(int, input().split())\n\n# TODO: print b and a, swapped\n');
        END IF;

        -- Sum of Diagonal of a Square Matrix
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'sum-of-diagonal-of-square-matrix') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40038-0000-4000-8000-000000000001', 'Sum of Diagonal of a Square Matrix', 'sum-of-diagonal-of-square-matrix', 'Medium',
                'The first line contains an integer n. The next n lines each contain n space-separated integers (an n x n matrix). Print the sum of the main diagonal (top-left to bottom-right).',
                '1 <= n <= 100', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40038-0000-4000-8000-000000000002', 'c4c40038-0000-4000-8000-000000000001', '2\n1 2\n3 4', '5', FALSE, 0),
                ('c4c40038-0000-4000-8000-000000000003', 'c4c40038-0000-4000-8000-000000000001', '3\n1 0 0\n0 1 0\n0 0 1', '3', FALSE, 1),
                ('c4c40038-0000-4000-8000-000000000004', 'c4c40038-0000-4000-8000-000000000001', '1\n5', '5', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40038-0000-4000-8000-000000000005', 'c4c40038-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\nmatrix = [list(map(int, input().split())) for _ in range(n)]\n\n# TODO: print the sum of the main diagonal\n');
        END IF;

        -- Check Perfect Number
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'check-perfect-number') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40039-0000-4000-8000-000000000001', 'Check Perfect Number', 'check-perfect-number', 'Easy',
                'A perfect number equals the sum of its proper divisors (excluding itself). Given n, print "true" if it is a perfect number, otherwise "false".',
                '1 <= n <= 10^6', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40039-0000-4000-8000-000000000002', 'c4c40039-0000-4000-8000-000000000001', '28', 'true', FALSE, 0),
                ('c4c40039-0000-4000-8000-000000000003', 'c4c40039-0000-4000-8000-000000000001', '12', 'false', FALSE, 1),
                ('c4c40039-0000-4000-8000-000000000004', 'c4c40039-0000-4000-8000-000000000001', '6', 'true', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40039-0000-4000-8000-000000000005', 'c4c40039-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print true or false\n');
        END IF;

        -- GCD of an Array
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'gcd-of-an-array') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40040-0000-4000-8000-000000000001', 'GCD of an Array', 'gcd-of-an-array', 'Easy',
                'Given a line of space-separated positive integers, print the GCD of all of them.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40040-0000-4000-8000-000000000002', 'c4c40040-0000-4000-8000-000000000001', '12 18 24', '6', FALSE, 0),
                ('c4c40040-0000-4000-8000-000000000003', 'c4c40040-0000-4000-8000-000000000001', '7 13 5', '1', FALSE, 1),
                ('c4c40040-0000-4000-8000-000000000004', 'c4c40040-0000-4000-8000-000000000001', '9', '9', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40040-0000-4000-8000-000000000005', 'c4c40040-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the GCD of all the numbers\n');
        END IF;

        -- Product of Array Elements
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'product-of-array-elements') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40041-0000-4000-8000-000000000001', 'Product of Array Elements', 'product-of-array-elements', 'Easy',
                'Given a line of space-separated integers, print the product of all of them.',
                '1 <= length <= 20, -20 <= each value <= 20', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40041-0000-4000-8000-000000000002', 'c4c40041-0000-4000-8000-000000000001', '1 2 3 4', '24', FALSE, 0),
                ('c4c40041-0000-4000-8000-000000000003', 'c4c40041-0000-4000-8000-000000000001', '5 0 3', '0', FALSE, 1),
                ('c4c40041-0000-4000-8000-000000000004', 'c4c40041-0000-4000-8000-000000000001', '2 2', '4', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40041-0000-4000-8000-000000000005', 'c4c40041-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the product of all the numbers\n');
        END IF;

        -- Count Duplicated Values
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'count-duplicated-values') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40042-0000-4000-8000-000000000001', 'Count Duplicated Values', 'count-duplicated-values', 'Easy',
                'Given a line of space-separated integers, print the number of distinct values that appear more than once.',
                '1 <= length <= 1000', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40042-0000-4000-8000-000000000002', 'c4c40042-0000-4000-8000-000000000001', '1 2 2 3 3 3 4', '2', FALSE, 0),
                ('c4c40042-0000-4000-8000-000000000003', 'c4c40042-0000-4000-8000-000000000001', '1 2 3', '0', FALSE, 1),
                ('c4c40042-0000-4000-8000-000000000004', 'c4c40042-0000-4000-8000-000000000001', '5 5 5 5', '1', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40042-0000-4000-8000-000000000005', 'c4c40042-0000-4000-8000-000000000001', 'PYTHON3',
                    'nums = list(map(int, input().split()))\n\n# TODO: print the number of distinct values that appear more than once\n');
        END IF;

        -- String to Integer
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'string-to-integer') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40043-0000-4000-8000-000000000001', 'String to Integer', 'string-to-integer', 'Easy',
                'Given a string representing an integer (optionally starting with a minus sign), print it as an integer.',
                '-10^9 <= value <= 10^9', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40043-0000-4000-8000-000000000002', 'c4c40043-0000-4000-8000-000000000001', '123', '123', FALSE, 0),
                ('c4c40043-0000-4000-8000-000000000003', 'c4c40043-0000-4000-8000-000000000001', '-45', '-45', FALSE, 1),
                ('c4c40043-0000-4000-8000-000000000004', 'c4c40043-0000-4000-8000-000000000001', '0', '0', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40043-0000-4000-8000-000000000005', 'c4c40043-0000-4000-8000-000000000001', 'PYTHON3',
                    's = input().strip()\n\n# TODO: parse and print the integer\n');
        END IF;

        -- Repeat String N Times
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'repeat-string-n-times') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40044-0000-4000-8000-000000000001', 'Repeat String N Times', 'repeat-string-n-times', 'Easy',
                'Given a line with a word and an integer n separated by a space, print the word repeated n times with no separator. If n is 0, print an empty line.',
                '0 <= n <= 1000, 1 <= word length <= 100', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40044-0000-4000-8000-000000000002', 'c4c40044-0000-4000-8000-000000000001', 'ab 3', 'ababab', FALSE, 0),
                ('c4c40044-0000-4000-8000-000000000003', 'c4c40044-0000-4000-8000-000000000001', 'x 0', '', FALSE, 1),
                ('c4c40044-0000-4000-8000-000000000004', 'c4c40044-0000-4000-8000-000000000001', 'hi 2', 'hihi', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40044-0000-4000-8000-000000000005', 'c4c40044-0000-4000-8000-000000000001', 'PYTHON3',
                    'word, n = input().split()\nn = int(n)\n\n# TODO: print word repeated n times\n');
        END IF;

        -- Most Frequent Character
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'most-frequent-character') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40045-0000-4000-8000-000000000001', 'Most Frequent Character', 'most-frequent-character', 'Medium',
                'Given a lowercase string with no spaces, print the character that occurs most often. If there is a tie, print whichever of the tied characters occurs first in the string.',
                '1 <= length <= 10^4', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40045-0000-4000-8000-000000000002', 'c4c40045-0000-4000-8000-000000000001', 'programming', 'r', FALSE, 0),
                ('c4c40045-0000-4000-8000-000000000003', 'c4c40045-0000-4000-8000-000000000001', 'aabbbcc', 'b', FALSE, 1),
                ('c4c40045-0000-4000-8000-000000000004', 'c4c40045-0000-4000-8000-000000000001', 'zzzz', 'z', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40045-0000-4000-8000-000000000005', 'c4c40045-0000-4000-8000-000000000001', 'PYTHON3',
                    's = input().strip()\n\n# TODO: print the most frequent character (first on ties)\n');
        END IF;

        -- Palindrome Number Check
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'palindrome-number-check') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES ('c4c40046-0000-4000-8000-000000000001', 'Palindrome Number Check', 'palindrome-number-check', 'Easy',
                'Given an integer n, print "true" if it reads the same forwards and backwards, otherwise "false". Negative numbers are never palindromes.',
                '-10^9 <= n <= 10^9', 2000, 128000);
            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order) VALUES
                ('c4c40046-0000-4000-8000-000000000002', 'c4c40046-0000-4000-8000-000000000001', '121', 'true', FALSE, 0),
                ('c4c40046-0000-4000-8000-000000000003', 'c4c40046-0000-4000-8000-000000000001', '123', 'false', FALSE, 1),
                ('c4c40046-0000-4000-8000-000000000004', 'c4c40046-0000-4000-8000-000000000001', '-121', 'false', TRUE, 2);
            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code) VALUES
                ('c4c40046-0000-4000-8000-000000000005', 'c4c40046-0000-4000-8000-000000000001', 'PYTHON3',
                    'n = int(input())\n\n# TODO: print true or false\n');
        END IF;

    END IF;
END$$
DELIMITER ;

CALL seed_more_judge_problems_0083();
DROP PROCEDURE seed_more_judge_problems_0083;
