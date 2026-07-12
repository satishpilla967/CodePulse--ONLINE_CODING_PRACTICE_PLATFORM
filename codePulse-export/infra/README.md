# `infra/`

> [!NOTE]
> Our staging to production migration script is located [here](https://github.com/tahminator/codepulse/tree/main/.github/scripts/copy-prod-db/index.ts#L28).

[`clean-stg-db.SQL`](./clean-stg-db.SQL) is a SQL script used to clean and scramble staging data after copying the production database.

This directory contains the Dockerfile used to build the main CodePulse image, which is then uploaded to [hub.docker.com/r/tahminator/codepulse](https://hub.docker.com/r/tahminator/codepulse).

The image is then deployed to the [DigitalOcean App Platform](https://www.digitalocean.com/products/app-platform) see [`.do/`](../.do/) to view the DigitalOcean app spec and more detailed documentation regarding DigitalOcean and deployments.

There is a Bun Shell script which helps us manage the workflow for deployments across production and staging, which can be found at [`.github/scripts/redeploy/index.ts`](../.github/scripts/redeploy/index.ts).

## Database: PostgreSQL → MySQL migration

CodePulse was originally built on PostgreSQL. As of this migration, the app runs on **MySQL 8**.

- All 76 original Postgres-native Flyway migrations (`V0001__...` through `V0076__...`) have been
  moved to [`db/archive-postgres/`](../db/archive-postgres/) — deliberately kept *outside*
  `db/migration/` (Flyway's filesystem locations scan recursively into subdirectories, so an
  archive folder nested inside `db/migration/` would still get picked up and re-run against MySQL).
  They are kept for historical reference only — do not move them back under `db/migration/`.
- [`db/migration/V0077__Consolidated_mysql_schema.sql`](../db/migration/V0077__Consolidated_mysql_schema.sql)
  is a fresh, MySQL-native migration that creates the full current schema (the union/latest state of
  all 76 archived migrations) from scratch. Any environment migrating from Postgres needs its data
  exported/transformed and imported separately — this migration only creates schema + the small
  Discord-club seed rows, it does not migrate existing row data.
- Translations applied going from Postgres to MySQL:
  - `UUID` columns → `CHAR(36)`.
  - `TIMESTAMP WITH TIME ZONE` → `TIMESTAMP` (app already normalizes to UTC `Instant` in Java).
  - Postgres native `CREATE TYPE ... AS ENUM` types → inline, column-scoped MySQL `ENUM(...)`.
    The historical duplicate-named `"Tag"` type (defined separately in V0023 and V0035) is a
    non-issue in MySQL since enums are column-scoped, not database-global.
  - `gen_random_uuid()` (used only in the DiscordClub/DiscordClubMetadata seed migrations
    V0060/V0070/V0073) → literal pre-generated UUID string constants.
  - `pgcrypto` extension (`V0019`) → dropped entirely, no longer needed.
  - The `job`/`lobby` table triggers + `pg_notify`/`LISTEN` (`V0054`, `V0057`) → dropped. The
    underlying `Job`/`Lobby` table structures are preserved; the notification mechanism itself was
    replaced with **Redis pub/sub** via `jedis` (see `scheduled/redis/NotifyListener.java` and the
    `publish(...)` calls in `JobSqlRepository`/`LobbySqlRepository`).
  - Columns searched case-insensitively via Postgres `ILIKE` in the Java layer
    (`User.discordName`/`leetcodeUsername`/`nickname`, `Question.questionTitle`, `Leaderboard.name`)
    use `utf8mb4_0900_ai_ci` collation in the new schema so a plain SQL `LIKE` behaves the same way.
- Local dev/test MySQL is provided via the root [`docker-compose.yml`](../docker-compose.yml)
  (`mysql:8` service). CI (`.github/workflows/ci-cd.yml`) spins up the same MySQL service for the
  test job instead of Postgres.
## Judge0-backed online judge (Phase B)

CodePulse now has an internally-authored problem catalog (`problem` / `problem_starter_code` /
`test_case`) with submissions run against a self-hosted **Judge0 CE** instance rather than polling
LeetCode's GraphQL API for a linked account's recent submissions.

- New tables: `problem`, `problem_starter_code`, `test_case`, `judge_submission`,
  `judge_submission_result` (see `db/migration/V0078__Judge0_problem_tables.sql`). `LobbyQuestion`
  gained a nullable `problemId` column so duels can be assigned an internally-authored problem;
  the legacy `questionBankId` column was made nullable rather than dropped, to avoid destroying
  existing duel history in this pass.
- `common/judge0/Judge0Client(Impl)` + `common/judge0/throttled/ThrottledJudge0Client(Impl)` talk to
  Judge0's REST API (`POST /submissions`, `GET /submissions/{token}`, and batch variants), mirroring
  the existing `LeetcodeClient`/`ThrottledLeetcodeClient` client/throttled-client pairing.
- `common/judge/Judge0Service` is the orchestrator: `runCode` (synchronous, ad-hoc, never
  persisted/scored) and `submitSolution` (persists a `PENDING` `judge_submission` + a
  `judge_submission_result` row per test case, then returns immediately).
  `scheduled/judge/Judge0PollingService` (`@Async` + `@Scheduled(fixedDelay = 5s)`, modeled on
  `LeetcodeQuestionProcessService.drainQueue`) polls Judge0 for unresolved tokens and finalizes the
  submission (verdict, points, leaderboard update) once every test case has resolved.
- `api/judge/JudgeController` exposes `GET /api/judge/problem/{id}`,
  `GET /api/judge/problem/{id}/starter-code/{language}`, `POST /api/judge/run`,
  `POST /api/judge/submit`, `GET /api/judge/submission/{id}`.
  `api/admin/JudgeAdminController` exposes admin CRUD for problems/test cases/starter code under
  `/api/admin/judge/**`.
- **Local dev only**: the root `docker-compose.yml` adds a Judge0 CE stack (`judge0-server`,
  `judge0-workers`, plus Judge0's own dedicated Postgres/Redis containers — isolated from the app's
  MySQL and the app's own `jedis` Redis, which are kept as separate containers to avoid coupling
  Judge0 internals to app cache/pub-sub behavior). Set `JUDGE0_BASE_URL` (defaults to
  `http://localhost:2358`) and optionally `JUDGE0_API_KEY` for the app to talk to it.
  **Production Judge0 deployment is a separate ops decision** — this compose file is not intended
  to be run in production as-is.

### LeetCode submission-polling removal (Phase B5) and POTD retention decision

The LeetCode submission-polling pipeline has been fully removed: `common/leetcode/**`
(`LeetcodeClient`/`ThrottledLeetcodeClient`, the submission/question/topic GraphQL query classes),
`common/submissions/SubmissionsHandler`, `scheduled/submission/SubmissionScheduler`,
`scheduled/leetcode/LeetcodeQuestionProcessService`, `FetchAllLeetcodeQuestions`,
`AttachTagsToExistingQuestion`, `api/submission/SubmissionController` (+ `LeetcodeUsernameObject`),
and the `/refresh` Discord slash command (`LeaderboardManager.refreshUserSubmissions`,
`DiscordClubManager.refreshSubmissions`) are all deleted, along with the LeetCode-account-avatar
lookup in `CustomAuthenticationSuccessHandler`'s login flow (Discord OAuth login itself is
untouched — only the LeetCode-linking side effect was removed).

**POTD is kept, but re-implemented as a narrow, self-contained client.** LeetCode's "Problem of the
Day" sync (`PotdSetter`) is a distinct, still-desired feature — separate from the submission-polling
pipeline being replaced — so instead of deleting it along with the rest of `common/leetcode/**`, its
one required capability (`getPotd()`, an unauthenticated public GraphQL query) was extracted into a
new, minimal `common/potd/leetcode/LeetcodePotdClient` + `LeetcodePotd` model, with its own
`resilience4j` `leetcodePotdClient` retry/circuitbreaker config (renamed from the old shared
`leetcodeClient` block in `application.yml`). `PotdSetter` now depends only on this narrow client,
not on the (now-deleted) full `LeetcodeClient`/`ThrottledLeetcodeClient`.

`LeaderboardManager` and `CustomAuthenticationSuccessHandler` no longer depend on any LeetCode
client. `DuelManager` was already fully rewired onto `problem`/`judge_submission` in an earlier pass.

Note: `scheduled/pg/handler/JobNotifyHandler` (fed by `scheduled/leetcode/RefetchIncompleteQuestionsService`
via the `Job`/`JobRepository` + `INSERT_JOB` Redis-pubsub-backed notify pipeline) previously drained
the now-deleted `LeetcodeQuestionProcessService`'s queue; since nothing else creates `Job` rows for it
to process, it's now a no-op stub. This whole `Job`-based question-sync chain
(`RefetchIncompleteQuestionsService` → `Job` insert → `JobNotifyHandler`) is effectively dead code
and a good candidate for a follow-up cleanup pass, but was left in place here since it's outside this
pass's LeetCode-client-removal scope and touching it risks breaking the `INSERT_JOB` notify wiring for
no functional gain. `User.leetcodeUsername` is likewise still read in several DTOs/UI (dashboard,
leaderboard, user profile) purely for display and was left alone; only the *linking* flow (onboarding
`UsernameForm`, `/api/leetcode/set`, `/api/leetcode/key`) was removed. A future migration could drop
the column once all display usages are confirmed unnecessary.

### Frontend judge UI (Phase B7)

- New route `/problem/:problemId` (`js/src/app/problem/[problemId]/Problem.page.tsx`) renders
  `JudgeWorkspace` (`js/src/app/problem/[problemId]/_components/JudgeWorkspace.tsx`): problem
  statement panel, Monaco editor (`@monaco-editor/react`) with a language selector and starter code
  loaded per-language, a custom-input textarea, Run Code / Submit Solution buttons, and a results
  panel (per-run stdout/stderr/compile output, or submission verdict + test case stats + points).
- `js/src/lib/api/queries/judge/index.ts` — `useProblemQuery`, `useStarterCodeQuery`,
  `useRunCodeMutation`, `useSubmitSolutionMutation`, `useSubmissionStatusQuery` (client-side polling
  every 2s while a submission is `PENDING`/`RUNNING`; SSE was not wired up for this pass — the
  existing duel SSE plumbing is duel-specific and polling was the more direct fit for this cut).
  Backing types were hand-added to `js/src/lib/api/types/schema.ts` (paths/operations/components for
  `/api/judge/**`) since that file is normally generated from a live backend OpenAPI spec that
  couldn't be run in this environment — regenerate it for real the next time the backend runs.
- `js/src/lib/api/schema/judge/index.ts` — zod validation schemas for the run/submit forms, matching
  the `schema/duel/` convention.
- `ActiveDuelBody.tsx`'s previously-stubbed Submit button now opens a modal embedding
  `JudgeWorkspace` scoped to the lobby's current problem (`questions[0]`) and lobby id, closing the
  modal automatically on an `ACCEPTED` verdict.
- LeetCode-username-linking UI removed: `js/src/app/onboarding/_components/UsernameForm.tsx` and
  `js/src/lib/api/queries/auth/leetcode/index.ts` (which called the now-deleted `/api/leetcode/set`
  and `/api/leetcode/key` endpoints) are deleted. `Onboarding.page.tsx` is simplified to redirect
  straight to `/dashboard` with a TODO — there is no defined replacement onboarding step, so none was
  invented.

### Discord bot (JDA) removal

The Discord-bot leaderboard/club integration has been fully removed. This included the entire `jda/`
package (`JDAClient`, `JDAClientManager`, slash-command handling), `common/components/DiscordClubManager`,
`scheduled/discord/WeeklyLeaderboard`, the `DiscordClub`/`DiscordClubMetadata` models and repositories
(`common/db/models/discord/**`, `common/db/repos/discord/**`), the Discord-club-specific
`/api/admin/discord/message*` routes, and the `net.dv8tion:JDA` dependency. The `DiscordClub`/
`DiscordClubMetadata` tables (created in `V0077__Consolidated_mysql_schema.sql`) are dropped via
`db/migration/V0080__Drop_discord_club_tables.sql`. This is unrelated to the plain-password `Club`
sign-up feature (`common/db/models/club/Club.java`), which is unaffected and remains live.

`common/reporter/Reporter`/`ThrottledReporter`, which previously posted error/log reports to a Discord
channel via JDA, now log via SLF4J instead (same public interface, no callers needed to change).

Discord OAuth login (`/api/auth/flow/discord`, `User.discordId`/`discordName`) is a separate concern
and was **not** touched by this removal — see the "LeetCode submission-polling removal" note above,
which already documents that Discord OAuth login itself remains untouched.

## All-in-one local Docker setup (`app` + `flyway` services)

For a machine that only has Docker installed (no Java/Maven/Node/pnpm needed), `docker-compose.yml`
can now run the entire stack, not just the infra:

- `app` builds and runs the full application from the existing `infra/Dockerfile` — that Dockerfile
  already builds the frontend and embeds its static output into the Spring Boot jar, so **one
  container serves both the API and the UI** on port 8080. There is no separate frontend/hot-reload
  container in this setup by design (see `dev.sh`/`dev.ps1` instead if you want fast iteration with
  `pnpm dev`'s HMR — those remain the right tool for active development).
- `flyway` is a one-shot migration runner using the official `flyway/flyway` image, pointed at
  `./db/migration`. It exists because the app's own `flyway-core`/`flyway-mysql` Maven dependencies
  are intentionally `provided` scope (build-time only, excluded from the runtime jar — see `pom.xml`),
  so the packaged `app` image cannot migrate itself. `app` waits for `flyway` to exit successfully
  (`condition: service_completed_successfully`) before starting.
- `redis` (the app's own Redis, separate from Judge0's internal one) was temporarily commented out
  during local debugging on a machine that already had a native `redis-server` bound to port 6379 —
  it's been restored here. On a fresh machine with nothing already listening on 6379 this will start
  normally; if you hit a port conflict, either stop the conflicting process or remap the host port via
  `REDIS_PORT` in `.env`.
- `app` and `flyway` are both behind the `full` Compose profile, so they are **not** started by a
  plain `docker compose up` (which `dev.sh`/`dev.ps1` use to bring up just the infra before running
  the backend/frontend natively via `mvnw`/`pnpm`). This avoids both a port-8080 conflict and an
  unnecessary image build for anyone using the native dev workflow.

Usage: `cp example.env .env` (fill in `SECRET_KEY` at minimum), then
`docker compose --profile full up -d --build`. First build takes a while (compiles the Java backend
and builds the frontend inside the containers); subsequent runs are fast unless
`pom.xml`/`js/package.json`/source changes require a rebuild.

**Known limitation this does not solve:** Judge0's sandbox (`isolate`) requires the *host* Linux
kernel to use the legacy cgroup v1 hierarchy. This is unrelated to Docker and cannot be fixed by
containerizing more of the stack — see the cgroup v1/v2 troubleshooting note earlier in this file's
project history (or the conversation/commit history) if `Run Code`/`Submit Solution` returns
`INTERNAL_ERROR` on a machine using cgroup v2 (the default on most recent Linux distros).
