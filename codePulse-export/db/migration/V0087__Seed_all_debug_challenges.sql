-- ============================================================================
-- Dev/staging seed data: buggy-code Debug Challenges for every problem seeded
-- in V0082/V0083/V0084 (except Two Sum, already seeded in V0086). Each is a
-- correct solution with exactly one intentional, findable bug (see inline
-- comments). PYTHON3 only, same trade-off as the starter-code/buggy-code batches.
-- ============================================================================
DELIMITER $$
CREATE PROCEDURE `seed_all_debug_challenges_0087`()
BEGIN
    IF DATABASE() <> 'codepulse-prod' THEN
        -- reverse-a-string
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'b2b2b2b2-0011-4000-8000-000000000011' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70001-0000-4000-8000-000000000001', 'b2b2b2b2-0011-4000-8000-000000000011', 'PYTHON3', 's = input()\nprint(s[::-1] + "x")  # bug: appends an extra stray character\n');
        END IF;

        -- fizzbuzz
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'b2b2b2b2-0021-4000-8000-000000000021' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70002-0000-4000-8000-000000000002', 'b2b2b2b2-0021-4000-8000-000000000021', 'PYTHON3', 'n = int(input())\nfor i in range(1, n + 1):\n    if i % 3 == 0 and i % 5 == 0:\n        print("Fizz")  # bug: should print "FizzBuzz" when divisible by both\n    elif i % 3 == 0:\n        print("Fizz")\n    elif i % 5 == 0:\n        print("Buzz")\n    else:\n        print(i)\n');
        END IF;

        -- palindrome-check
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'b2b2b2b2-0031-4000-8000-000000000031' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70003-0000-4000-8000-000000000003', 'b2b2b2b2-0031-4000-8000-000000000031', 'PYTHON3', 's = input().strip()\nprint("true" if s == s[::-1] else "true")  # bug: always prints true\n');
        END IF;

        -- sum-of-array
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40001-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70004-0000-4000-8000-000000000004', 'c4c40001-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nprint(sum(nums) + 1)  # bug: off-by-one, adds 1 to the sum\n');
        END IF;

        -- maximum-of-array
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40002-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70005-0000-4000-8000-000000000005', 'c4c40002-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nprint(min(nums))  # bug: prints min instead of max\n');
        END IF;

        -- count-vowels
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40003-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70006-0000-4000-8000-000000000006', 'c4c40003-0000-4000-8000-000000000001', 'PYTHON3', 'text = input()\ncount = 0\nfor ch in text:\n    if ch in "aeiou":\n        count += 1\nprint(count + 1)  # bug: off-by-one\n');
        END IF;

        -- factorial
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40004-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70007-0000-4000-8000-000000000007', 'c4c40004-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nresult = 1\nfor i in range(1, n):  # bug: should be range(1, n + 1), misses the last factor\n    result *= i\nprint(result)\n');
        END IF;

        -- nth-fibonacci-number
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40005-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70008-0000-4000-8000-000000000008', 'c4c40005-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\na, b = 0, 1\nfor _ in range(n):\n    a, b = b, a + b\nprint(b)  # bug: should print a, this is off by one Fibonacci index\n');
        END IF;

        -- is-prime
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40006-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70009-0000-4000-8000-000000000009', 'c4c40006-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nif n < 2:\n    print("false")\nelse:\n    is_p = True\n    for i in range(2, n):  # bug: should be range(2, int(n**0.5)+1); works but O(n) is fine, real bug below\n        if n % i == 0:\n            is_p = False\n            break\n    print("true" if is_p else "true")  # bug: always prints true\n');
        END IF;

        -- greatest-common-divisor
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40007-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70010-0000-4000-8000-000000000010', 'c4c40007-0000-4000-8000-000000000001', 'PYTHON3', 'a, b = map(int, input().split())\nwhile b:\n    a, b = b, a % b\nprint(a + 1)  # bug: off-by-one\n');
        END IF;

        -- least-common-multiple
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40008-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70011-0000-4000-8000-000000000011', 'c4c40008-0000-4000-8000-000000000001', 'PYTHON3', 'a, b = map(int, input().split())\nx, y = a, b\nwhile y:\n    x, y = y, x % y\nprint(a * b // x - 1)  # bug: off-by-one\n');
        END IF;

        -- reverse-an-integer
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40009-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70012-0000-4000-8000-000000000012', 'c4c40009-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nsign = -1 if n < 0 else 1\ndigits = str(abs(n))\nprint(sign * int(digits))  # bug: forgot to reverse the digits\n');
        END IF;

        -- sum-of-digits
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40010-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70013-0000-4000-8000-000000000013', 'c4c40010-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\ntotal = 0\nfor ch in str(n):\n    total += int(ch)\nprint(total * 2)  # bug: doubles the result\n');
        END IF;

        -- count-digits
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40011-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70014-0000-4000-8000-000000000014', 'c4c40011-0000-4000-8000-000000000001', 'PYTHON3', 'n = input().strip()\nprint(len(n) - 1)  # bug: off-by-one\n');
        END IF;

        -- power-of-two-check
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40012-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70015-0000-4000-8000-000000000015', 'c4c40012-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nprint("true" if (n & (n - 1)) != 0 else "true")  # bug: always prints true\n');
        END IF;

        -- armstrong-number-check
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40013-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70016-0000-4000-8000-000000000016', 'c4c40013-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\ndigits = str(n)\npower = len(digits)\ntotal = sum(int(d) ** power for d in digits)\nprint("true" if total == n else "true")  # bug: always prints true\n');
        END IF;

        -- binary-to-decimal
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40014-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70017-0000-4000-8000-000000000017', 'c4c40014-0000-4000-8000-000000000001', 'PYTHON3', 'binary = input().strip()\nprint(int(binary, 2) + 1)  # bug: off-by-one\n');
        END IF;

        -- decimal-to-binary
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40015-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70018-0000-4000-8000-000000000018', 'c4c40015-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nprint(bin(n)[2:] if n != 0 else "1")  # bug: should print "0" for n == 0\n');
        END IF;

        -- anagram-check
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40016-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70019-0000-4000-8000-000000000019', 'c4c40016-0000-4000-8000-000000000001', 'PYTHON3', 'a, b = input().split()\nprint("true" if sorted(a) == sorted(b) else "true")  # bug: always prints true\n');
        END IF;

        -- count-distinct-elements
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40017-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70020-0000-4000-8000-000000000020', 'c4c40017-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nprint(len(set(nums)) + 1)  # bug: off-by-one\n');
        END IF;

        -- find-missing-number
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40018-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70021-0000-4000-8000-000000000021', 'c4c40018-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nn = len(nums) + 1\nexpected = n * (n + 1) // 2\nprint(expected - sum(nums) + 1)  # bug: off-by-one\n');
        END IF;

        -- second-largest-distinct-element
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40019-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70022-0000-4000-8000-000000000022', 'c4c40019-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\ndistinct = sorted(set(nums), reverse=True)\nprint(distinct[0])  # bug: prints the largest instead of the second largest\n');
        END IF;

        -- bubble-sort-ascending
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40020-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70023-0000-4000-8000-000000000023', 'c4c40020-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nnums.sort(reverse=True)  # bug: sorts descending instead of ascending\nprint(" ".join(map(str, nums)))\n');
        END IF;

        -- linear-search
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40021-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70024-0000-4000-8000-000000000024', 'c4c40021-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\ntarget = int(input())\nresult = -1\nfor i, num in enumerate(nums):\n    if num == target:\n        result = i\nprint(result + 1)  # bug: off-by-one on the found index\n');
        END IF;

        -- binary-search
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40022-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70025-0000-4000-8000-000000000025', 'c4c40022-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\ntarget = int(input())\nlo, hi = 0, len(nums) - 1\nresult = -1\nwhile lo <= hi:\n    mid = (lo + hi) // 2\n    if nums[mid] == target:\n        result = mid\n        break\n    elif nums[mid] < target:\n        lo = mid + 1\n    else:\n        hi = mid - 1\nprint(result + 1 if result != -1 else -1)  # bug: off-by-one when found\n');
        END IF;

        -- merge-two-sorted-arrays
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40023-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70026-0000-4000-8000-000000000026', 'c4c40023-0000-4000-8000-000000000001', 'PYTHON3', 'a = list(map(int, input().split()))\nb = list(map(int, input().split()))\nmerged = sorted(a + b)\nprint(" ".join(map(str, merged[:-1])))  # bug: drops the last element\n');
        END IF;

        -- rotate-array-right-by-k
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40024-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70027-0000-4000-8000-000000000027', 'c4c40024-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nk = int(input())\nn = len(nums)\nk = k % n if n else 0\nrotated = nums[-k:] + nums[:-k] if k else nums\nprint(" ".join(map(str, nums)))  # bug: prints the original array, not rotated\n');
        END IF;

        -- maximum-subarray-sum
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40025-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70028-0000-4000-8000-000000000028', 'c4c40025-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nbest = nums[0]\ncurrent = nums[0]\nfor num in nums[1:]:\n    current = max(num, current + num)\n    best = max(best, current)\nprint(best + 1)  # bug: off-by-one\n');
        END IF;

        -- valid-parentheses
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40026-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70029-0000-4000-8000-000000000029', 'c4c40026-0000-4000-8000-000000000001', 'PYTHON3', 's = input().strip()\npairs = {")": "(", "]": "[", "}": "{"}\nstack = []\nok = True\nfor ch in s:\n    if ch in "([{":\n        stack.append(ch)\n    else:\n        if not stack or stack.pop() != pairs[ch]:\n            ok = False\n            break\nprint("true" if ok else "true")  # bug: always prints true\n');
        END IF;

        -- count-words-in-a-sentence
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40027-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70030-0000-4000-8000-000000000030', 'c4c40027-0000-4000-8000-000000000001', 'PYTHON3', 'text = input()\nprint(len(text.split()) + 1)  # bug: off-by-one\n');
        END IF;

        -- capitalize-first-letter-of-each-word
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40028-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70031-0000-4000-8000-000000000031', 'c4c40028-0000-4000-8000-000000000001', 'PYTHON3', 'text = input()\nwords = text.split()\nprint(" ".join(w.upper() for w in words))  # bug: uppercases the whole word, not just the first letter\n');
        END IF;

        -- run-length-encoding
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40029-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70032-0000-4000-8000-000000000032', 'c4c40029-0000-4000-8000-000000000001', 'PYTHON3', 's = input().strip()\nresult = []\ni = 0\nwhile i < len(s):\n    j = i\n    while j < len(s) and s[j] == s[i]:\n        j += 1\n    result.append(s[i] + str(j - i + 1))  # bug: off-by-one on the count\n    i = j\nprint("".join(result))\n');
        END IF;

        -- longest-word-in-a-sentence
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40030-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70033-0000-4000-8000-000000000033', 'c4c40030-0000-4000-8000-000000000001', 'PYTHON3', 'words = input().split()\nshortest = words[0]\nfor w in words:\n    if len(w) < len(shortest):  # bug: tracks the shortest word instead of the longest\n        shortest = w\nprint(shortest)\n');
        END IF;

        -- sort-array-descending
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40031-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70034-0000-4000-8000-000000000034', 'c4c40031-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nnums.sort()  # bug: sorts ascending instead of descending\nprint(" ".join(map(str, nums)))\n');
        END IF;

        -- sum-of-even-numbers
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40032-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70035-0000-4000-8000-000000000035', 'c4c40032-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nprint(sum(n for n in nums if n % 2 != 0))  # bug: sums odd numbers instead of even\n');
        END IF;

        -- sum-of-odd-numbers
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40033-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70036-0000-4000-8000-000000000036', 'c4c40033-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nprint(sum(n for n in nums if n % 2 == 0))  # bug: sums even numbers instead of odd\n');
        END IF;

        -- check-leap-year
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40034-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70037-0000-4000-8000-000000000037', 'c4c40034-0000-4000-8000-000000000001', 'PYTHON3', 'year = int(input())\nis_leap = (year % 4 == 0) and (year % 100 != 0)  # bug: missing the "or year % 400 == 0" exception\nprint("true" if is_leap else "false")\n');
        END IF;

        -- celsius-to-fahrenheit
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40035-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70038-0000-4000-8000-000000000038', 'c4c40035-0000-4000-8000-000000000001', 'PYTHON3', 'c = float(input())\nf = c * 9 / 5 + 32\nprint(f"{f + 1:.1f}")  # bug: off-by-one degree\n');
        END IF;

        -- count-set-bits
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40036-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70039-0000-4000-8000-000000000039', 'c4c40036-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nprint(bin(n).count("1") + 1)  # bug: off-by-one\n');
        END IF;

        -- swap-two-numbers
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40037-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70040-0000-4000-8000-000000000040', 'c4c40037-0000-4000-8000-000000000001', 'PYTHON3', 'a, b = input().split()\nprint(a, b)  # bug: forgot to actually swap\n');
        END IF;

        -- sum-of-diagonal-of-square-matrix
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40038-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70041-0000-4000-8000-000000000041', 'c4c40038-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nmatrix = [list(map(int, input().split())) for _ in range(n)]\ntotal = sum(matrix[i][i] for i in range(n))\nprint(total + 1)  # bug: off-by-one\n');
        END IF;

        -- check-perfect-number
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40039-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70042-0000-4000-8000-000000000042', 'c4c40039-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\ndivisors_sum = sum(i for i in range(1, n) if n % i == 0)\nprint("true" if divisors_sum == n else "true")  # bug: always prints true\n');
        END IF;

        -- gcd-of-an-array
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40040-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70043-0000-4000-8000-000000000043', 'c4c40040-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nresult = nums[0]\nfor num in nums[1:]:\n    a, b = result, num\n    while b:\n        a, b = b, a % b\n    result = a\nprint(result + 1)  # bug: off-by-one\n');
        END IF;

        -- product-of-array-elements
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40041-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70044-0000-4000-8000-000000000044', 'c4c40041-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nproduct = 1\nfor num in nums:\n    product *= num\nprint(product + 1)  # bug: off-by-one\n');
        END IF;

        -- count-duplicated-values
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40042-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70045-0000-4000-8000-000000000045', 'c4c40042-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nfrom collections import Counter\ncounts = Counter(nums)\nprint(sum(1 for v in counts.values() if v >= 1))  # bug: should be v > 1 (counts every distinct value, not just duplicated)\n');
        END IF;

        -- string-to-integer
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40043-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70046-0000-4000-8000-000000000046', 'c4c40043-0000-4000-8000-000000000001', 'PYTHON3', 's = input().strip()\nprint(int(s) + 1)  # bug: off-by-one\n');
        END IF;

        -- repeat-string-n-times
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40044-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70047-0000-4000-8000-000000000047', 'c4c40044-0000-4000-8000-000000000001', 'PYTHON3', 'word, n = input().split()\nn = int(n)\nprint(word * (n + 1))  # bug: off-by-one repeat count\n');
        END IF;

        -- most-frequent-character
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40045-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70048-0000-4000-8000-000000000048', 'c4c40045-0000-4000-8000-000000000001', 'PYTHON3', 's = input().strip()\nfrom collections import Counter\ncounts = Counter(s)\nbest_char = None\nbest_count = -1\nfor ch in reversed(s):  # bug: iterates in reverse, so ties resolve to the LAST occurrence instead of the first\n    if counts[ch] > best_count:\n        best_count = counts[ch]\n        best_char = ch\nprint(best_char)\n');
        END IF;

        -- palindrome-number-check
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'c4c40046-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70049-0000-4000-8000-000000000049', 'c4c40046-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nif n < 0:\n    print("false")\nelse:\n    s = str(n)\n    print("true" if s == s[::-1] else "true")  # bug: always prints true when non-negative\n');
        END IF;

        -- count-consonants
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50001-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70050-0000-4000-8000-000000000050', 'd5d50001-0000-4000-8000-000000000001', 'PYTHON3', 'text = input()\ncount = 0\nfor ch in text:\n    if ch.isalpha() and ch not in "aeiou":\n        count += 1\nprint(count - 1)  # bug: off-by-one\n');
        END IF;

        -- sum-of-squares
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50002-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70051-0000-4000-8000-000000000051', 'd5d50002-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nprint(sum(n * n for n in nums) + 1)  # bug: off-by-one\n');
        END IF;

        -- average-of-array
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50003-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70052-0000-4000-8000-000000000052', 'd5d50003-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\navg = sum(nums) / len(nums)\nprint(f"{avg:.1f}")  # bug: wrong decimal precision (should be 2 places)\n');
        END IF;

        -- check-even-or-odd
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50004-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70053-0000-4000-8000-000000000053', 'd5d50004-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nprint("Odd" if n % 2 == 0 else "Even")  # bug: labels swapped\n');
        END IF;

        -- multiplication-table
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50005-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70054-0000-4000-8000-000000000054', 'd5d50005-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nprint(" ".join(str(n * i) for i in range(1, 10)))  # bug: only goes up to 9, should go up to 10\n');
        END IF;

        -- ascii-value-of-character
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50006-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70055-0000-4000-8000-000000000055', 'd5d50006-0000-4000-8000-000000000001', 'PYTHON3', 'c = input()\nprint(ord(c) + 1)  # bug: off-by-one\n');
        END IF;

        -- character-from-ascii-value
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50007-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70056-0000-4000-8000-000000000056', 'd5d50007-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nprint(chr(n + 1))  # bug: off-by-one\n');
        END IF;

        -- vowel-or-consonant
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50008-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70057-0000-4000-8000-000000000057', 'd5d50008-0000-4000-8000-000000000001', 'PYTHON3', 'c = input()\nprint("Vowel" if c in "aeiou" else "Vowel")  # bug: always prints Vowel\n');
        END IF;

        -- sum-of-first-n-natural-numbers
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50009-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70058-0000-4000-8000-000000000058', 'd5d50009-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nprint(n * (n + 1) // 2 + 1)  # bug: off-by-one\n');
        END IF;

        -- digital-root
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50010-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70059-0000-4000-8000-000000000059', 'd5d50010-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nif n >= 10:  # bug: should loop (while), only reduces once instead of repeating until a single digit\n    n = sum(int(d) for d in str(n))\nprint(n)\n');
        END IF;

        -- trailing-zeros-in-factorial
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50011-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70060-0000-4000-8000-000000000060', 'd5d50011-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\ncount = 0\npower = 5\nwhile power <= n:\n    count += n // power\n    power *= 5\nprint(count + 1)  # bug: off-by-one\n');
        END IF;

        -- smallest-element-in-array
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50012-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70061-0000-4000-8000-000000000061', 'd5d50012-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nprint(max(nums))  # bug: prints max instead of min\n');
        END IF;

        -- array-contains-value
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50013-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70062-0000-4000-8000-000000000062', 'd5d50013-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\ntarget = int(input())\nprint("true" if target in nums else "true")  # bug: always prints true\n');
        END IF;

        -- concatenate-two-strings
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50014-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70063-0000-4000-8000-000000000063', 'd5d50014-0000-4000-8000-000000000001', 'PYTHON3', 'a, b = input().split()\nprint(b + a)  # bug: concatenated in the wrong order\n');
        END IF;

        -- string-length
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50015-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70064-0000-4000-8000-000000000064', 'd5d50015-0000-4000-8000-000000000001', 'PYTHON3', 'text = input()\nprint(len(text) + 1)  # bug: off-by-one\n');
        END IF;

        -- check-string-rotation
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50016-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70065-0000-4000-8000-000000000065', 'd5d50016-0000-4000-8000-000000000001', 'PYTHON3', 'a, b = input().split()\nprint("true" if b in (a + a) else "true")  # bug: always prints true\n');
        END IF;

        -- longest-common-prefix
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50017-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70066-0000-4000-8000-000000000066', 'd5d50017-0000-4000-8000-000000000001', 'PYTHON3', 'words = input().split()\nprefix = words[0]\nfor w in words[1:]:\n    while not w.startswith(prefix):\n        prefix = prefix[:-1]\n        if not prefix:\n            break\nprint(prefix + "!")  # bug: appends a stray character\n');
        END IF;

        -- longest-palindromic-substring-length
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50018-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70067-0000-4000-8000-000000000067', 'd5d50018-0000-4000-8000-000000000001', 'PYTHON3', 's = input().strip()\nn = len(s)\nbest = 1\nfor center in range(n):\n    for l, r in ((center, center), (center, center + 1)):\n        while l >= 0 and r < n and s[l] == s[r]:\n            l -= 1\n            r += 1\n        best = max(best, r - l - 1)\nprint(best - 1)  # bug: off-by-one\n');
        END IF;

        -- group-anagrams-count
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50019-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70068-0000-4000-8000-000000000068', 'd5d50019-0000-4000-8000-000000000001', 'PYTHON3', 'words = input().split()\ngroups = set()\nfor w in words:\n    groups.add(w)  # bug: should group by sorted(w), this treats every distinct word as its own group\nprint(len(groups))\n');
        END IF;

        -- subarray-sum-equals-k
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50020-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70069-0000-4000-8000-000000000069', 'd5d50020-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nk = int(input())\ncount = 0\nprefix_sums = {0: 1}\nrunning = 0\nfor num in nums:\n    running += num\n    count += prefix_sums.get(running - k, 0)\n    prefix_sums[running] = prefix_sums.get(running, 0) + 1\nprint(count + 1)  # bug: off-by-one\n');
        END IF;

        -- merge-intervals
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50021-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70070-0000-4000-8000-000000000070', 'd5d50021-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nintervals = sorted((nums[i], nums[i + 1]) for i in range(0, len(nums), 2))\nmerged = [list(intervals[0])]\nfor start, end in intervals[1:]:\n    if start <= merged[-1][1]:\n        merged[-1][1] = max(merged[-1][1], end)\n    else:\n        merged.append([start, end])\nflat = [str(x) for pair in merged for x in pair]\nprint(" ".join(flat[:-1]))  # bug: drops the last value\n');
        END IF;

        -- rotate-matrix-90-degrees
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50022-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70071-0000-4000-8000-000000000071', 'd5d50022-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nmatrix = [list(map(int, input().split())) for _ in range(n)]\nrotated = [[matrix[i][j] for i in range(n)] for j in range(n)]  # bug: only transposes, never reverses each row\nfor row in rotated:\n    print(" ".join(map(str, row)))\n');
        END IF;

        -- spiral-order-matrix
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50023-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70072-0000-4000-8000-000000000072', 'd5d50023-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\nmatrix = [list(map(int, input().split())) for _ in range(n)]\nresult = []\ntop, bottom, left, right = 0, n - 1, 0, n - 1\nwhile top <= bottom and left <= right:\n    for c in range(right, left - 1, -1):  # bug: top row is traversed right-to-left instead of left-to-right\n        result.append(matrix[top][c])\n    top += 1\n    for r in range(top, bottom + 1):\n        result.append(matrix[r][right])\n    right -= 1\n    if top <= bottom:\n        for c in range(right, left - 1, -1):\n            result.append(matrix[bottom][c])\n        bottom -= 1\n    if left <= right:\n        for r in range(bottom, top - 1, -1):\n            result.append(matrix[r][left])\n        left += 1\nprint(" ".join(map(str, result)))\n');
        END IF;

        -- word-break-check
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50024-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70073-0000-4000-8000-000000000073', 'd5d50024-0000-4000-8000-000000000001', 'PYTHON3', 's = input().strip()\nwords = set(input().split())\nn = len(s)\ndp = [False] * (n + 1)\ndp[0] = True\nfor i in range(1, n + 1):\n    for j in range(i):\n        if dp[j] and s[j:i] in words:\n            dp[i] = True\n            break\nprint("true" if dp[n] else "true")  # bug: always prints true\n');
        END IF;

        -- coin-change-minimum-coins
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50025-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70074-0000-4000-8000-000000000074', 'd5d50025-0000-4000-8000-000000000001', 'PYTHON3', 'coins = list(map(int, input().split()))\namount = int(input())\nINF = float("inf")\ndp = [0] + [INF] * amount\nfor i in range(1, amount + 1):\n    for c in coins:\n        if c <= i:\n            dp[i] = min(dp[i], dp[i - c] + 1)\nprint(dp[amount] + 1 if dp[amount] != INF else -1)  # bug: off-by-one on the coin count\n');
        END IF;

        -- house-robber-max
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50026-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70075-0000-4000-8000-000000000075', 'd5d50026-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nprev, curr = 0, 0\nfor num in nums:\n    prev, curr = curr, max(curr, prev + num)\nprint(curr + 1)  # bug: off-by-one\n');
        END IF;

        -- climbing-stairs-ways
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50027-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70076-0000-4000-8000-000000000076', 'd5d50027-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\na, b = 1, 1\nfor _ in range(n - 1):\n    a, b = b, a + b\nprint(a + 1)  # bug: off-by-one\n');
        END IF;

        -- unique-paths-in-grid
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50028-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70077-0000-4000-8000-000000000077', 'd5d50028-0000-4000-8000-000000000001', 'PYTHON3', 'm, n = map(int, input().split())\ndp = [[1] * n for _ in range(m)]\nfor i in range(1, m):\n    for j in range(1, n):\n        dp[i][j] = dp[i - 1][j] + dp[i][j - 1]\nprint(dp[m - 1][n - 1] + 1)  # bug: off-by-one\n');
        END IF;

        -- longest-increasing-subsequence-length
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50029-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70078-0000-4000-8000-000000000078', 'd5d50029-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\ndp = [1] * len(nums)\nfor i in range(len(nums)):\n    for j in range(i):\n        if nums[j] < nums[i]:\n            dp[i] = max(dp[i], dp[j] + 1)\nprint(max(dp) + 1)  # bug: off-by-one\n');
        END IF;

        -- number-of-islands
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50030-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70079-0000-4000-8000-000000000079', 'd5d50030-0000-4000-8000-000000000001', 'PYTHON3', 'rows = int(input())\ngrid = [list(input().strip()) for _ in range(rows)]\ncols = len(grid[0])\n\ncount = 0\nfor r in range(rows):\n    for c in range(cols):\n        if grid[r][c] == "1":  # bug: counts every land cell as its own island instead of flood-filling connected groups\n            count += 1\nprint(count)\n');
        END IF;

        -- course-schedule-possible
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50031-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70080-0000-4000-8000-000000000080', 'd5d50031-0000-4000-8000-000000000001', 'PYTHON3', 'n, m = map(int, input().split())\nedges = [tuple(map(int, input().split())) for _ in range(m)]\ngraph = {i: [] for i in range(n)}\nindegree = [0] * n\nfor a, b in edges:\n    graph[b].append(a)\n    indegree[a] += 1\nqueue = [i for i in range(n) if indegree[i] == 0]\nvisited = 0\nwhile queue:\n    node = queue.pop()\n    visited += 1\n    for nxt in graph[node]:\n        indegree[nxt] -= 1\n        if indegree[nxt] == 0:\n            queue.append(nxt)\nprint("true" if visited == n else "true")  # bug: always prints true\n');
        END IF;

        -- kth-largest-element
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50032-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70081-0000-4000-8000-000000000081', 'd5d50032-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nk = int(input())\nnums.sort()  # bug: sorts ascending, so indexing below finds the kth SMALLEST, not largest\nprint(nums[k - 1])\n');
        END IF;

        -- top-k-frequent-elements
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50033-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70082-0000-4000-8000-000000000082', 'd5d50033-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nk = int(input())\nfrom collections import Counter\ncounts = Counter(nums)\nseen_order = []\nfor n in nums:\n    if n not in seen_order:\n        seen_order.append(n)\nranked = sorted(seen_order, key=lambda x: -counts[x])\nprint(" ".join(map(str, ranked[: k + 1])))  # bug: off-by-one, returns k+1 elements instead of k\n');
        END IF;

        -- container-with-most-water
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50034-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70083-0000-4000-8000-000000000083', 'd5d50034-0000-4000-8000-000000000001', 'PYTHON3', 'heights = list(map(int, input().split()))\nlo, hi = 0, len(heights) - 1\nbest = 0\nwhile lo < hi:\n    best = max(best, (hi - lo))  # bug: forgot to multiply by min(heights[lo], heights[hi]), just tracks max width\n    if heights[lo] < heights[hi]:\n        lo += 1\n    else:\n        hi -= 1\nprint(best)\n');
        END IF;

        -- product-of-array-except-self
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50035-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70084-0000-4000-8000-000000000084', 'd5d50035-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nn = len(nums)\nresult = [1] * n\nleft = 1\nfor i in range(n):\n    result[i] = left\n    left *= nums[i]\nright = 1\nfor i in range(n - 1, -1, -1):\n    result[i] *= right\n    # bug: forgot to update `right *= nums[i]`, so only the left-product is ever applied\nprint(" ".join(map(str, result)))\n');
        END IF;

        -- edit-distance
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50036-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70085-0000-4000-8000-000000000085', 'd5d50036-0000-4000-8000-000000000001', 'PYTHON3', 'a, b = input().split()\nm, n = len(a), len(b)\ndp = [[0] * (n + 1) for _ in range(m + 1)]\nfor i in range(m + 1):\n    dp[i][0] = i\nfor j in range(n + 1):\n    dp[0][j] = j\nfor i in range(1, m + 1):\n    for j in range(1, n + 1):\n        if a[i - 1] == b[j - 1]:\n            dp[i][j] = dp[i - 1][j - 1]\n        else:\n            dp[i][j] = 1 + min(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])\nprint(dp[m][n] + 1)  # bug: off-by-one\n');
        END IF;

        -- longest-common-subsequence-length
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50037-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70086-0000-4000-8000-000000000086', 'd5d50037-0000-4000-8000-000000000001', 'PYTHON3', 'a, b = input().split()\nm, n = len(a), len(b)\ndp = [[0] * (n + 1) for _ in range(m + 1)]\nfor i in range(1, m + 1):\n    for j in range(1, n + 1):\n        if a[i - 1] == b[j - 1]:\n            dp[i][j] = dp[i - 1][j - 1] + 1\n        else:\n            dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])\nprint(dp[m][n] - 1 if dp[m][n] > 0 else 0)  # bug: off-by-one\n');
        END IF;

        -- median-of-two-sorted-arrays
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50038-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70087-0000-4000-8000-000000000087', 'd5d50038-0000-4000-8000-000000000001', 'PYTHON3', 'a = list(map(int, input().split()))\nb = list(map(int, input().split()))\nmerged = sorted(a + b)\nn = len(merged)\nif n % 2 == 1:\n    print(f"{merged[n // 2]:.1f}")\nelse:\n    print(f"{merged[n // 2]:.1f}")  # bug: for even length should average the two middle elements, this just takes one\n');
        END IF;

        -- trapping-rain-water
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50039-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70088-0000-4000-8000-000000000088', 'd5d50039-0000-4000-8000-000000000001', 'PYTHON3', 'heights = list(map(int, input().split()))\nn = len(heights)\nleft_max = [0] * n\nright_max = [0] * n\nleft_max[0] = heights[0]\nfor i in range(1, n):\n    left_max[i] = max(left_max[i - 1], heights[i])\nright_max[n - 1] = heights[n - 1]\nfor i in range(n - 2, -1, -1):\n    right_max[i] = max(right_max[i + 1], heights[i])\ntotal = 0\nfor i in range(n):\n    total += min(left_max[i], right_max[i]) - heights[i]\nprint(total + 1)  # bug: off-by-one\n');
        END IF;

        -- n-queens-count
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50040-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70089-0000-4000-8000-000000000089', 'd5d50040-0000-4000-8000-000000000001', 'PYTHON3', 'n = int(input())\ncount = 0\n\ndef solve(row, cols, diag1, diag2):\n    global count\n    if row == n:\n        count += 1\n        return\n    for col in range(n):\n        if col in cols or (row - col) in diag1 or (row + col) in diag2:\n            continue\n        solve(row + 1, cols | {col}, diag1 | {row - col}, diag2 | {row + col})\n\nsolve(0, set(), set(), set())\nprint(count + 1)  # bug: off-by-one\n');
        END IF;

        -- word-ladder-length
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50041-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70090-0000-4000-8000-000000000090', 'd5d50041-0000-4000-8000-000000000001', 'PYTHON3', 'begin = input().strip()\nend = input().strip()\nword_list = set(input().split())\nif end not in word_list:\n    print(0)\nelse:\n    from collections import deque\n    queue = deque([(begin, 1)])\n    visited = {begin}\n    result = 0\n    while queue:\n        word, steps = queue.popleft()\n        if word == end:\n            result = steps\n            break\n        for i in range(len(word)):\n            for c in "abcdefghijklmnopqrstuvwxyz":\n                candidate = word[:i] + c + word[i + 1:]\n                if candidate in word_list and candidate not in visited:\n                    visited.add(candidate)\n                    queue.append((candidate, steps + 1))\n    print(result + 1 if result else 0)  # bug: off-by-one when a path is found\n');
        END IF;

        -- minimum-path-sum-in-grid
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50042-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70091-0000-4000-8000-000000000091', 'd5d50042-0000-4000-8000-000000000001', 'PYTHON3', 'rows, cols = map(int, input().split())\ngrid = [list(map(int, input().split())) for _ in range(rows)]\ndp = [[0] * cols for _ in range(rows)]\ndp[0][0] = grid[0][0]\nfor j in range(1, cols):\n    dp[0][j] = dp[0][j - 1] + grid[0][j]\nfor i in range(1, rows):\n    dp[i][0] = dp[i - 1][0] + grid[i][0]\nfor i in range(1, rows):\n    for j in range(1, cols):\n        dp[i][j] = grid[i][j] + min(dp[i - 1][j], dp[i][j - 1])\nprint(dp[rows - 1][cols - 1] - 1)  # bug: off-by-one\n');
        END IF;

        -- longest-valid-parentheses
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50043-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70092-0000-4000-8000-000000000092', 'd5d50043-0000-4000-8000-000000000001', 'PYTHON3', 's = input().strip()\nstack = [-1]\nbest = 0\nfor i, ch in enumerate(s):\n    if ch == "(":\n        stack.append(i)\n    else:\n        stack.pop()\n        if not stack:\n            stack.append(i)\n        else:\n            best = max(best, i - stack[-1])\nprint(best + 1)  # bug: off-by-one\n');
        END IF;

        -- maximum-product-subarray
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50044-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70093-0000-4000-8000-000000000093', 'd5d50044-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nbest = cur_max = cur_min = nums[0]\nfor num in nums[1:]:\n    candidates = (num, cur_max * num, cur_min * num)\n    cur_max = max(candidates)\n    cur_min = min(candidates)\n    best = max(best, cur_max)\nprint(best - 1)  # bug: off-by-one\n');
        END IF;

        -- regular-expression-matching
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50045-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70094-0000-4000-8000-000000000094', 'd5d50045-0000-4000-8000-000000000001', 'PYTHON3', 's = input()\np = input()\nm, n = len(s), len(p)\ndp = [[False] * (n + 1) for _ in range(m + 1)]\ndp[0][0] = True\nfor j in range(1, n + 1):\n    if p[j - 1] == "*":\n        dp[0][j] = dp[0][j - 2]\nfor i in range(1, m + 1):\n    for j in range(1, n + 1):\n        if p[j - 1] == "*":\n            dp[i][j] = dp[i][j - 2] or (\n                (p[j - 2] == "." or p[j - 2] == s[i - 1]) and dp[i - 1][j]\n            )\n        else:\n            dp[i][j] = dp[i - 1][j - 1] and (p[j - 1] == "." or p[j - 1] == s[i - 1])\nprint("true" if dp[m][n] else "true")  # bug: always prints true\n');
        END IF;

        -- sliding-window-maximum
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50046-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70095-0000-4000-8000-000000000095', 'd5d50046-0000-4000-8000-000000000001', 'PYTHON3', 'nums = list(map(int, input().split()))\nk = int(input())\nfrom collections import deque\ndq = deque()\nresult = []\nfor i, num in enumerate(nums):\n    while dq and nums[dq[-1]] <= num:\n        dq.pop()\n    dq.append(i)\n    if dq[0] <= i - k:\n        dq.popleft()\n    if i >= k - 1:\n        result.append(nums[dq[0]])\nprint(" ".join(map(str, result[:-1])))  # bug: drops the last window''s result\n');
        END IF;

        -- merge-k-sorted-arrays
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50047-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70096-0000-4000-8000-000000000096', 'd5d50047-0000-4000-8000-000000000001', 'PYTHON3', 'k = int(input())\narrays = [list(map(int, input().split())) for _ in range(k)]\nmerged = []\nfor arr in arrays:\n    merged.extend(arr)\nmerged.sort(reverse=True)  # bug: sorts descending instead of ascending\nprint(" ".join(map(str, merged)))\n');
        END IF;

        -- dijkstra-shortest-path
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50048-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70097-0000-4000-8000-000000000097', 'd5d50048-0000-4000-8000-000000000001', 'PYTHON3', 'import heapq\nn, m = map(int, input().split())\nedges = [tuple(map(int, input().split())) for _ in range(m)]\ngraph = {i: [] for i in range(n)}\nfor u, v, w in edges:\n    graph[u].append((v, w))\nsource = int(input())\ndist = [float("inf")] * n\ndist[source] = 1  # bug: source distance should be 0, not 1\npq = [(0, source)]\nwhile pq:\n    d, node = heapq.heappop(pq)\n    if d > dist[node]:\n        continue\n    for nxt, w in graph[node]:\n        nd = d + w\n        if nd <= dist[nxt]:  # bug: should be strictly less than; ties can cause incorrect relaxation order but not shown here, real bug below\n            dist[nxt] = nd\n            heapq.heappush(pq, (nd, nxt))\nprint(" ".join(str(d) if d != float("inf") else -1 for d in dist))\n');
        END IF;

        -- knapsack-maximum-value
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50049-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70098-0000-4000-8000-000000000098', 'd5d50049-0000-4000-8000-000000000001', 'PYTHON3', 'capacity = int(input())\nn = int(input())\nweights = list(map(int, input().split()))\nvalues = list(map(int, input().split()))\ndp = [0] * (capacity + 1)\nfor i in range(n):\n    for c in range(capacity, weights[i] - 1, -1):\n        dp[c] = max(dp[c], dp[c - weights[i]] + values[i])\nprint(dp[capacity] + 1)  # bug: off-by-one\n');
        END IF;

        -- longest-palindromic-subsequence-length
        IF NOT EXISTS (SELECT 1 FROM `problem_buggy_code` WHERE problem_id = 'd5d50050-0000-4000-8000-000000000001' AND language = 'PYTHON3') THEN
            INSERT INTO `problem_buggy_code` (id, problem_id, language, buggy_code)
            VALUES ('f7f70099-0000-4000-8000-000000000099', 'd5d50050-0000-4000-8000-000000000001', 'PYTHON3', 's = input().strip()\nn = len(s)\ndp = [[0] * n for _ in range(n)]\nfor i in range(n - 1, -1, -1):\n    dp[i][i] = 1\n    for j in range(i + 1, n):\n        if s[i] == s[j]:\n            dp[i][j] = dp[i + 1][j - 1] + 2\n        else:\n            dp[i][j] = max(dp[i + 1][j], dp[i][j - 1])\nprint(dp[0][n - 1] - 1 if n > 1 else dp[0][0])  # bug: off-by-one\n');
        END IF;

    END IF;
END$$
DELIMITER ;

CALL seed_all_debug_challenges_0087();
DROP PROCEDURE seed_all_debug_challenges_0087;
