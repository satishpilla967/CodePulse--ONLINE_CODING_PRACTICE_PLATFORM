# `src/`

This directory holds the source code for the CodePulse backend, which is written in [Java](https://www.java.com/en/) and [Spring Boot](https://spring.io/projects/spring-boot).

The backend is currently running on [`Java 25`](https://www.oracle.com/java/technologies/javase/25-relnote-issues.html#NewFeature) due to the fact that virtual threads would be quite useful for us since:

- our production machine doesn't have many threads (1 vCPU, 2 threads)
- a lot of our backend complexity comes from background processes such as timely leaderboard syncs and long-lived Postgres [`NOTIFY/LISTEN`] logic for pub-sub.

View the public Swagger endpoint in production at [codepulse.patinanetwork.org/swagger-ui/index.html](https://codepulse.patinanetwork.org/swagger-ui/index.html)

View [STYLEGUIDE.md](../STYLEGUIDE.md) to view best practices.

## Requirements

> [!WARNING]
> It is HIGHLY recommended that you follow the setup instructions at [docs/local/README.md](../docs/local/README.md) to get up and running.

- [`openjdk@25`](https://openjdk.org/projects/jdk/25/) (though you may feel free to use any other distribution if you would like)
- [`mvn`](https://maven.apache.org/what-is-maven.html) - you do not need to explicitly download this, just use `./mvnw` from the root directory instead (our `Justfile` commands all use `./mvnw` as well).

## Run

> [!WARNING]
> It is HIGHLY recommended that you follow the setup instructions at [docs/local/README.md](../docs/local/README.md) to get up and running.

## Structure

> [!NOTE]
> The backend is very large, so many classes are merged into a package instead to save space for the high-level structure overview

```
src/main
├── test                                                            # Holds all tests
├── java
│   └── org
│       └── patinanetwork
│           └── codepulse
│               ├── api                                             # holds all api specific classes (controllers, request bodies, custom validators, etc)
│               ├── CodePulseApplication.java                       # spring boot entrypoint
│               ├── common
│               │   ├── components                                  # managers that are responsible for complex behaviors (e.g. DuelManager, etc.)
│               │   ├── db
│               │   │   ├── helper                                  # helper utils
│               │   │   ├── models                                  # db model objects
│               │   │   └── repos                                   # db repository classes
│               │   ├── dto                                         # dtos
│               │   ├── email
│               │   │   ├── client
│               │   │   │   ├── codepulse                           # used to send emails
│               │   │   │   └── github                              # used to read emails during Leetcode OAuth process
│               │   │   ├── Email.java                              # base interface used by ./client
│               │   │   ├── error                                   # shared error used by ./client
│               │   │   ├── Message.java                            # base message object used by ./client
│               │   │   ├── options                                 # shared builder objects used by ./client
│               │   │   └── template                                # react email templater
│               │   ├── jwt                                         # jwt helper class(es)
│               │   ├── leetcode                                    # leetcode client classes
│               │   │   ├── LeetcodeClient.java
│               │   │   ├── LeetcodeClientImpl.java
│               │   │   ├── models                                  # shared objects belonging to leetcode.com
│               │   │   ├── queries                                 # GraphQL queries stored as strings
│               │   │   ├── score                                   # scoring logic that CodePulse uses
│               │   │   └── throttled                               # throttled to help alleviate rate limits
│               │   │       ├── ThrottledLeetcodeClient.java
│               │   │       └── ThrottledLeetcodeClientImpl.java
│               │   ├── redis                                       # WIP: redis
│               │   ├── reporter                                    # classes that can be used to report logs & errors via the application logger
│               │   ├── schools                                     # school-specific utils
│               │   ├── security                                    # security/auth helper utils
│               │   ├── simpleredis                                 # in-memory redis
│               │   ├── submissions                                 # leetcode/codepulse submission glue code handler
│               │   ├── time                                        # time utils
│               │   ├── url
│               │   │   └── ServerUrlUtils.java
│               │   └── utils                                       # more utils
│               ├── playwright                                      # external playwright client
│               │   └── PlaywrightClient.java
│               ├── scheduled                                       # holds all background processes
│               │   ├── auth                                        # used to "steal" a leetcode token. this is a special class as runs in the bg and can be imported into other classes (this separation should be more distinct in the future)
│               │   ├── duel                                        # WIP: used to cleanup expired duels
│               │   ├── leetcode                                    # various bg services related to leetcode.com and CodePulse
│               │   ├── pg                                          # postgres NOTIFY/LISTEN handlers
│               │   │   ├── handler
│               │   │   │   ├── JobNotifyHandler.java
│               │   │   │   └── LobbyNotifyHandler.java
│               │   │   ├── NotifyListener.java                     # main class where notifications are checked
│               │   ├── potd                                        # update POTD
│               │   └── submission                                  # routinely check for new submissions
│               ├── shared                                          # WIP: still being worked out but currently used to define complex shared enum logic that can be used in the backend but also auto-generated to the frontend
│               │   └── tag
│               │       └── ParentTags.java
│               └── utilities
│                   ├── exception                                   # exception handlers
│                   ├── generator                                   # complex generator logic, such as automatic typescript file generation, react email generation, etc
│                   ├── OpenApiConfig.java                          # force loads some routes to OpenAPI spec
│                   ├── RateLimitingFilter.java                     # handles rate limiting
│                   ├── ServerMetadataObject.java                   # small object returned when /api called
│                   ├── sha                                         # object that returns the last committed SHA at runtime
│                   ├── StaticContentFilter.java                    # handles static content vs dynamic routes
│                   └── WebConfig.java                              # used to load some custom annotation stuff into Spring Security
└── resources
    ├── application-prod.yml                                        # prod overrides
    ├── application-stg.yml                                         # staging overrides
    ├── application.yml                                             # main yaml file
```

## Authentication

### Custom Routes

- **GET: `/api/auth/validate`** - Verifies whether the user is authenticated based on cookies stored in the browser.
- **GET: `/api/auth/logout`** - Logs out the user by invalidating the session and removing cookies from the browser. It automatically returns back to the frontend with either:
  - `/login?success=false&message=This is the failure message`.
  - `/login?success=true&message=This is the success message!`
  - These should be handled on the frontend route.

### Email + Password Routes

- **POST: `/api/auth/register`** - Creates a new account from an email/password/optional nickname, hashes the password with bcrypt, creates a session, and sets the `session_token` cookie.
- **POST: `/api/auth/login`** - Verifies an email/password pair against the stored bcrypt hash, creates a session, and sets the `session_token` cookie. Returns a generic 401 on failure (does not leak whether the email exists).

### Security Details

- **CSRF Protection** - Automatically managed by Spring Security, so no additional configuration is required.
- **Auth Validator / Session Token Cookie Setter** - Managed by `AuthSessionService`, shared by both `register` and `login` in `AuthController`.
  - Cookie Settings:
    - Name: `session_token`
    - Max Age: **30 days** (configurable via a private variable).

### Backend Objects

- [`Protector.java`](https://github.com/tahminator/codepulse/tree/main/src/main/java/org/patinanetwork/codepulse/common/security/Protector.java) is used to validate whether the user is logged in or not. It automatically handles unauthorized requests (and any other `ResponseStatusException`) via [ControllerExceptionHandler.java](https://github.com/tahminator/codepulse/tree/main/src/main/java/org/patinanetwork/codepulse/utilities/exception/ControllerExceptionHandler.java)

- [`Protected.java`](https://github.com/tahminator/codepulse/tree/main/src/main/java/org/patinanetwork/codepulse/common/security/annotation/Protected.java) can be applied to a controller method as an annotation. You can find an example inside of the file's Javadoc.
  - [`AuthController.java`](https://github.com/tahminator/codepulse/tree/main/src/main/java/org/patinanetwork/codepulse/api/auth/AuthController.java) contains examples of using Protector.java to protect endpoints.

- [`GlobalExceptionHandler.java`](https://github.com/tahminator/codepulse/blob/main/src/main/java/org/patinanetwork/codepulse/utilities/GlobalExceptionHandler.java) manages exception handling for unauthorized requests

- [`SecurityConfig.java`](https://github.com/tahminator/codepulse/blob/main/src/main/java/org/patinanetwork/codepulse/api/auth/security/SecurityConfig.java) holds the security filter chains and the `BCryptPasswordEncoder` bean
  - [`AuthSessionService.java`](https://github.com/tahminator/codepulse/tree/main/src/main/java/org/patinanetwork/codepulse/api/auth/security/AuthSessionService.java) handles the shared post-authentication logic (creating a session, setting the cookie, adding a new user to the current leaderboard) used by both `register` and `login`

### Examples

#### Annotation

You may use the [`Protected`](https://github.com/tahminator/codepulse/tree/main/src/main/java/org/patinanetwork/codepulse/common/security/annotation/Protected.java) annotation to validate requests manually like so:

```java
@RestController
@RequestMapping("/api")
public class ApiController {

    @PostMapping("/any-user")
    public ResponseEntity<ApiResponder<Empty>> apiAnyUser(@Protected final AuthenticationObject authenticationObject) {
        User user = authenticationObject.getUser(); // guaranteed for user to exist at this line.
    }

    @PostMapping("/admin-only")
    public ResponseEntity<ApiResponder<Empty>> apiForAdmins(@Protected(admin = true) final AuthenticationObject authenticationObject) {
        User user = authenticationObject.getUser(); // guaranteed for an admin user to exist at this line.
    }
}
```

#### Imperative

You may use the [`Protector`](https://github.com/tahminator/codepulse/tree/main/src/main/java/org/patinanetwork/codepulse/common/security/Protector.java) class to validate requests manually like so:

```java
@RestController
@RequestMapping("/api")
public class ApiController {

    private final Protector protector;

    public AdminController(final Protector protector) {
        this.protector = protector;
    }

    @PostMapping("/any-user")
    public ResponseEntity<ApiResponder<Empty>> apiAnyUser(final HttpServletRequest request) {
        AuthenticationObject authenticationObject = protector.validateSession(request);
        User user = authenticationObject.getUser(); // guaranteed for user to exist at this line.
    }

    @PostMapping("/admin-only")
    public ResponseEntity<ApiResponder<Empty>> apiForAdmins(final HttpServletRequest request) {
        AuthenticationObject authenticationObject = protector.validateAdminSession(request);
        User user = authenticationObject.getUser(); // guaranteed for an admin user to exist at this line.
    }
}
```

## Clients

### `Judge0Client`

Problems are now solved in-app via a self-hosted Judge0 CE instance, rather than by polling LeetCode submissions. `Judge0Client`/`Judge0ClientImpl` (`common/judge0/`) submit source code + test cases to Judge0 and poll for a verdict; `ThrottledJudge0Client` wraps it with bucket4j rate limiting, mirroring the old LeetCode client's pattern. `Judge0Service` (`common/judge/`) is the business-logic orchestrator: it handles "Run Code" (ad-hoc, unscored), "Submit Solution" (batched against all test cases, scored), and verdict computation. `Judge0PollingService` (`scheduled/judge/`) asynchronously drains pending submissions and finalizes results (see `db/migration/V0078__Judge0_problem_tables.sql` for the schema).

### `LeetcodePotdClient`

The only remaining LeetCode integration is `common/potd/leetcode/LeetcodePotdClient.java`, a small, unauthenticated client that fetches LeetCode's official "Problem of the Day" for display — this does not require session cookies, GitHub-based auth stealing, or Playwright (all of which have been removed along with the rest of the submission-polling pipeline).

### `Reporter`

CodePulse has a hand-rolled error reporter that will report certain logs and errors via the application's SLF4J logger.

#### Implementation Detail

- [`Reporter.java`](https://github.com/tahminator/codepulse/blob/main/src/main/java/org/patinanetwork/codepulse/common/reporter/Reporter.java) is the abstracted reporter class that exposes the following methods:
  - [`.log`](https://github.com/tahminator/codepulse/blob/main/src/main/java/org/patinanetwork/codepulse/common/reporter/Reporter.java#L73-L100) is used for non-errors that need to be reported back to the server. We mainly use this for tracking odd behaviors or suspicious activity that may not be an error.
  - [`.error`](https://github.com/tahminator/codepulse/blob/main/src/main/java/org/patinanetwork/codepulse/common/reporter/Reporter.java#L44-L71) is used to report any errors back to the server. We use this to raise an indicator to the dev team that something is wrong when it likely shouldn't be. We have logic that will automatically call `.error` on any [controller exceptions](https://github.com/tahminator/codepulse/blob/main/src/main/java/org/patinanetwork/codepulse/utilities/exception/ControllerExceptionHandler.java#L30-L43) or [task scheduler](https://github.com/tahminator/codepulse/blob/main/src/main/java/org/patinanetwork/codepulse/utilities/exception/ScheduledTaskExceptionHandler.java#L29-L48) (aka any background services).
- [`ReporterController.java`](https://github.com/tahminator/codepulse/blob/main/src/main/java/org/patinanetwork/codepulse/api/reporter/ReporterController.java) allows us to ingest errors from our endpoint. The endpoints have basic CSRF protections via checking the `Origin` header.
  - [`/api/reporter/error`](https://github.com/tahminator/codepulse/blob/main/src/main/java/org/patinanetwork/codepulse/api/reporter/ReporterController.java#L57-L70) is the endpoint used to ingest errors from the frontend which will be sent to the server.
  - [`/api/reporter/log`](https://github.com/tahminator/codepulse/blob/main/src/main/java/org/patinanetwork/codepulse/api/reporter/ReporterController.java#L57-L70) is the endpoint used to ingest logs from the frontend which will be sent to the server.
- [`ThrottledReporter.java`](https://github.com/tahminator/codepulse/blob/main/src/main/java/org/patinanetwork/codepulse/common/reporter/throttled/ThrottledReporter.java) is a rate-limited version of the Reporter class, which exposes the same functions but limits how often they can be called. Used for randomly-selected high-traffic logging.

#### Examples

[Link to Reporter example](https://github.com/tahminator/codepulse/blob/main/src/main/java/org/patinanetwork/codepulse/scheduled/auth/LeetcodeAuthStealer.java#L244-L259)

```java
    public synchronized String getCsrf() {
        if (csrf == null && !reported) {
            reported = true;
            reporter.log(Report.builder()
                            .environments(env.getActiveProfiles())
                            .location(Location.BACKEND)
                            .data("CSRF token is missing inside of LeetcodeAuthStealer. This may be something to look into.")
                            .build());
        }

        return csrf;
    }
```

[Link to ThrottledReporter example](https://github.com/tahminator/codepulse/blob/main/src/main/java/org/patinanetwork/codepulse/common/submissions/SubmissionsHandler.java#L131-L148)

```java
            throttledReporter.log(Report.builder()
                                            .data(String.format("""
                                                Score Distribution Report

                                                Leetcode Username: %s
                                                Difficulty: %s (%d pts)
                                                Acceptance Rate: %.2f
                                                Question Multiplier: %.2f
                                                Total: %d
                                                """,
                                                user.getLeetcodeUsername(),
                                                leetcodeQuestion.getDifficulty(),
                                                basePoints,
                                                leetcodeQuestion.getAcceptanceRate(),
                                                multiplier,
                                                points
                                                ))
                                            .build());
```
