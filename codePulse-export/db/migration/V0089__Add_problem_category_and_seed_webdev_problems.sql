-- Adds a `category` column to `problem` (DSA vs WEBDEV) so the frontend can split the problem list
-- into a DSA section and a Web Dev section, and seeds 5 real Web Dev problems to back that section
-- (previously the frontend only had 5 hardcoded, non-clickable placeholder cards). Web Dev problems are
-- still judged via stdin/stdout like every other problem here (there is no browser/DOM execution in this
-- judge), so they're framed as web-adjacent text-processing tasks (parsing query strings, slugs, HTTP
-- status categories, HTML tag counting) rather than literal UI-building exercises.

ALTER TABLE `problem`
    ADD COLUMN `category` ENUM('DSA', 'WEBDEV') NOT NULL DEFAULT 'DSA' AFTER `difficulty`;

DELIMITER $$
CREATE PROCEDURE `seed_webdev_problems_0089`()
BEGIN
    IF DATABASE() <> 'codepulse-prod' THEN

        -- --------------------------------------------------------------
        -- Parse Query String
        -- --------------------------------------------------------------
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'parse-query-string') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, category, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES (
                'c3c3c3c3-0001-4000-8000-000000000001',
                'Parse Query String',
                'parse-query-string',
                'Easy',
                'WEBDEV',
                'Given a URL query string (the part after `?`, without the leading `?`) on a single line, print each key=value pair on its own line, sorted alphabetically by key. Keys and values are already URL-decoded.',
                '1 <= length of input <= 500\nKeys are unique.',
                2000,
                128000
            );

            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order)
            VALUES
                ('c3c3c3c3-0002-4000-8000-000000000002', 'c3c3c3c3-0001-4000-8000-000000000001', 'b=2&a=1&c=3', 'a=1\nb=2\nc=3', FALSE, 0),
                ('c3c3c3c3-0003-4000-8000-000000000003', 'c3c3c3c3-0001-4000-8000-000000000001', 'name=alice&age=30', 'age=30\nname=alice', FALSE, 1),
                ('c3c3c3c3-0004-4000-8000-000000000004', 'c3c3c3c3-0001-4000-8000-000000000001', 'z=1&y=2&x=3&w=4', 'w=4\nx=3\ny=2\nz=1', TRUE, 2);

            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code)
            VALUES
                ('c3c3c3c3-0005-4000-8000-000000000005', 'c3c3c3c3-0001-4000-8000-000000000001', 'PYTHON3',
                    'query = input()\n\n# TODO: split on "&", then "=", sort by key, and print "key=value" per line\n'),
                ('c3c3c3c3-0006-4000-8000-000000000006', 'c3c3c3c3-0001-4000-8000-000000000001', 'JAVASCRIPT',
                    'const query = require("fs").readFileSync(0, "utf8").trim();\n\n// TODO: split on "&", then "=", sort by key, and print "key=value" per line\n');
        END IF;

        -- --------------------------------------------------------------
        -- Validate Email Format
        -- --------------------------------------------------------------
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'validate-email-format') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, category, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES (
                'c3c3c3c3-0011-4000-8000-000000000011',
                'Validate Email Format',
                'validate-email-format',
                'Easy',
                'WEBDEV',
                'Given a single line of text, print "true" if it is a syntactically valid email address (exactly one "@", a non-empty local part with no spaces, and a domain part containing at least one "." with non-empty labels on both sides), otherwise print "false".',
                '1 <= length of input <= 200',
                2000,
                128000
            );

            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order)
            VALUES
                ('c3c3c3c3-0012-4000-8000-000000000012', 'c3c3c3c3-0011-4000-8000-000000000011', 'user@example.com', 'true', FALSE, 0),
                ('c3c3c3c3-0013-4000-8000-000000000013', 'c3c3c3c3-0011-4000-8000-000000000011', 'not-an-email', 'false', FALSE, 1),
                ('c3c3c3c3-0014-4000-8000-000000000014', 'c3c3c3c3-0011-4000-8000-000000000011', 'a@b@c.com', 'false', TRUE, 2);

            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code)
            VALUES
                ('c3c3c3c3-0015-4000-8000-000000000015', 'c3c3c3c3-0011-4000-8000-000000000011', 'PYTHON3',
                    'text = input()\n\n# TODO: validate the email format and print "true" or "false"\n'),
                ('c3c3c3c3-0016-4000-8000-000000000016', 'c3c3c3c3-0011-4000-8000-000000000011', 'JAVASCRIPT',
                    'const text = require("fs").readFileSync(0, "utf8").trim();\n\n// TODO: validate the email format and print "true" or "false"\n');
        END IF;

        -- --------------------------------------------------------------
        -- Generate URL Slug
        -- --------------------------------------------------------------
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'generate-url-slug') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, category, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES (
                'c3c3c3c3-0021-4000-8000-000000000021',
                'Generate URL Slug',
                'generate-url-slug',
                'Easy',
                'WEBDEV',
                'Given a line of text (e.g. a blog post title), print a URL-friendly slug: lowercase the text, replace any run of characters that are not letters or digits with a single hyphen, and trim leading/trailing hyphens.',
                '1 <= length of input <= 200',
                2000,
                128000
            );

            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order)
            VALUES
                ('c3c3c3c3-0022-4000-8000-000000000022', 'c3c3c3c3-0021-4000-8000-000000000021', 'Hello World!', 'hello-world', FALSE, 0),
                ('c3c3c3c3-0023-4000-8000-000000000023', 'c3c3c3c3-0021-4000-8000-000000000021', '  Learn   CSS Grid -- Fast  ', 'learn-css-grid-fast', FALSE, 1),
                ('c3c3c3c3-0024-4000-8000-000000000024', 'c3c3c3c3-0021-4000-8000-000000000021', '**Top 10 JS Tips (2024)**', 'top-10-js-tips-2024', TRUE, 2);

            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code)
            VALUES
                ('c3c3c3c3-0025-4000-8000-000000000025', 'c3c3c3c3-0021-4000-8000-000000000021', 'PYTHON3',
                    'text = input()\n\n# TODO: build a lowercase, hyphen-separated slug from text\n'),
                ('c3c3c3c3-0026-4000-8000-000000000026', 'c3c3c3c3-0021-4000-8000-000000000021', 'JAVASCRIPT',
                    'const text = require("fs").readFileSync(0, "utf8").replace(/\\n$/, "");\n\n// TODO: build a lowercase, hyphen-separated slug from text\n');
        END IF;

        -- --------------------------------------------------------------
        -- HTTP Status Code Category
        -- --------------------------------------------------------------
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'http-status-code-category') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, category, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES (
                'c3c3c3c3-0031-4000-8000-000000000031',
                'HTTP Status Code Category',
                'http-status-code-category',
                'Easy',
                'WEBDEV',
                'Given an integer HTTP status code, print its category: "Informational" (100-199), "Success" (200-299), "Redirection" (300-399), "Client Error" (400-499), "Server Error" (500-599), or "Unknown" for anything else.',
                '0 <= statusCode <= 999',
                2000,
                128000
            );

            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order)
            VALUES
                ('c3c3c3c3-0032-4000-8000-000000000032', 'c3c3c3c3-0031-4000-8000-000000000031', '404', 'Client Error', FALSE, 0),
                ('c3c3c3c3-0033-4000-8000-000000000033', 'c3c3c3c3-0031-4000-8000-000000000031', '204', 'Success', FALSE, 1),
                ('c3c3c3c3-0034-4000-8000-000000000034', 'c3c3c3c3-0031-4000-8000-000000000031', '999', 'Unknown', TRUE, 2);

            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code)
            VALUES
                ('c3c3c3c3-0035-4000-8000-000000000035', 'c3c3c3c3-0031-4000-8000-000000000031', 'PYTHON3',
                    'status_code = int(input())\n\n# TODO: print the correct category for status_code\n'),
                ('c3c3c3c3-0036-4000-8000-000000000036', 'c3c3c3c3-0031-4000-8000-000000000031', 'JAVASCRIPT',
                    'const statusCode = parseInt(require("fs").readFileSync(0, "utf8").trim(), 10);\n\n// TODO: print the correct category for statusCode\n');
        END IF;

        -- --------------------------------------------------------------
        -- Count HTML Open Tags
        -- --------------------------------------------------------------
        IF NOT EXISTS (SELECT 1 FROM `problem` WHERE slug = 'count-html-open-tags') THEN
            INSERT INTO `problem` (id, title, slug, difficulty, category, statement, constraints, time_limit_ms, memory_limit_kb)
            VALUES (
                'c3c3c3c3-0041-4000-8000-000000000041',
                'Count HTML Open Tags',
                'count-html-open-tags',
                'Medium',
                'WEBDEV',
                'The first line is a snippet of HTML. The second line is a tag name (e.g. "div"). Print how many opening tags of that name appear in the snippet (e.g. `<div>` or `<div class="x">`, but not `</div>` or self-closing `<div/>`), case-insensitively.',
                '1 <= length of HTML <= 2000',
                2000,
                128000
            );

            INSERT INTO `test_case` (id, problem_id, input, expected_output, is_hidden, display_order)
            VALUES
                ('c3c3c3c3-0042-4000-8000-000000000042', 'c3c3c3c3-0041-4000-8000-000000000041', '<div><p>hi</p><div class="x">bye</div></div>\ndiv', '3', FALSE, 0),
                ('c3c3c3c3-0043-4000-8000-000000000043', 'c3c3c3c3-0041-4000-8000-000000000041', '<span>a</span><SPAN>b</SPAN>\nspan', '2', FALSE, 1),
                ('c3c3c3c3-0044-4000-8000-000000000044', 'c3c3c3c3-0041-4000-8000-000000000041', '<br/><div><br/></div>\nbr', '0', TRUE, 2);

            INSERT INTO `problem_starter_code` (id, problem_id, language, starter_code)
            VALUES
                ('c3c3c3c3-0045-4000-8000-000000000045', 'c3c3c3c3-0041-4000-8000-000000000041', 'PYTHON3',
                    'html = input()\ntag = input()\n\n# TODO: count opening <tag ...> occurrences, case-insensitively\n'),
                ('c3c3c3c3-0046-4000-8000-000000000046', 'c3c3c3c3-0041-4000-8000-000000000041', 'JAVASCRIPT',
                    'const lines = require("fs").readFileSync(0, "utf8").split("\\n");\nconst html = lines[0];\nconst tag = lines[1].trim();\n\n// TODO: count opening <tag ...> occurrences, case-insensitively\n');
        END IF;

    END IF;
END$$
DELIMITER ;

CALL seed_webdev_problems_0089();
DROP PROCEDURE seed_webdev_problems_0089;
