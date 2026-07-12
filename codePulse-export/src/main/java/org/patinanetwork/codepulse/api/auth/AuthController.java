package org.patinanetwork.codepulse.api.auth;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.patinanetwork.codepulse.api.auth.body.EmailBody;
import org.patinanetwork.codepulse.api.auth.body.LoginBody;
import org.patinanetwork.codepulse.api.auth.body.PasswordResetConfirmBody;
import org.patinanetwork.codepulse.api.auth.body.PasswordResetRequestBody;
import org.patinanetwork.codepulse.api.auth.body.RegisterBody;
import org.patinanetwork.codepulse.api.auth.security.AuthSessionService;
import org.patinanetwork.codepulse.common.db.models.Session;
import org.patinanetwork.codepulse.common.db.models.user.User;
import org.patinanetwork.codepulse.common.db.models.usertag.UserTag;
import org.patinanetwork.codepulse.common.db.repos.session.SessionRepository;
import org.patinanetwork.codepulse.common.db.repos.user.UserRepository;
import org.patinanetwork.codepulse.common.db.repos.usertag.UserTagRepository;
import org.patinanetwork.codepulse.common.dto.ApiResponder;
import org.patinanetwork.codepulse.common.dto.Empty;
import org.patinanetwork.codepulse.common.dto.autogen.UnsafeGenericFailureResponse;
import org.patinanetwork.codepulse.common.dto.security.AuthenticationObjectDto;
import org.patinanetwork.codepulse.common.dto.user.UserDto;
import org.patinanetwork.codepulse.common.email.client.codepulse.OfficialCodePulseEmailClient;
import org.patinanetwork.codepulse.common.email.error.EmailException;
import org.patinanetwork.codepulse.common.email.options.SendEmailOptions;
import org.patinanetwork.codepulse.common.email.template.ReactEmailTemplater;
import org.patinanetwork.codepulse.common.jwt.JWTClient;
import org.patinanetwork.codepulse.common.lag.FakeLag;
import org.patinanetwork.codepulse.common.reporter.Reporter;
import org.patinanetwork.codepulse.common.reporter.report.Report;
import org.patinanetwork.codepulse.common.schools.SchoolEnum;
import org.patinanetwork.codepulse.common.schools.magic.MagicLink;
import org.patinanetwork.codepulse.common.security.AuthenticationObject;
import org.patinanetwork.codepulse.common.security.Protector;
import org.patinanetwork.codepulse.common.security.annotation.Protected;
import org.patinanetwork.codepulse.common.security.passwordreset.PasswordResetToken;
import org.patinanetwork.codepulse.common.simpleredis.SimpleRedis;
import org.patinanetwork.codepulse.common.simpleredis.SimpleRedisProvider;
import org.patinanetwork.codepulse.common.simpleredis.SimpleRedisSlot;
import org.patinanetwork.codepulse.common.url.ServerUrlUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@Tag(name = "Authentication Routes")
@RequestMapping("/api/auth")
@Timed(value = "controller.execution")
public class AuthController {

    private static final double SECONDS_TO_WAIT = 10;

    private final SessionRepository sessionRepository;
    private final Protector protector;
    private final JWTClient jwtClient;
    private final UserRepository userRepository;
    private final OfficialCodePulseEmailClient emailClient;
    private final ServerUrlUtils serverUrlUtils;
    private final UserTagRepository userTagRepository;
    private final Reporter reporter;
    private final ReactEmailTemplater reactEmailTemplater;
    private final SimpleRedis<Long> simpleRedis;
    private final AuthSessionService authSessionService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            final SessionRepository sessionRepository,
            final Protector protector,
            final JWTClient jwtClient,
            final UserRepository userRepository,
            final OfficialCodePulseEmailClient emailClient,
            final ServerUrlUtils serverUrlUtils,
            final UserTagRepository userTagRepository,
            final Reporter reporter,
            final ReactEmailTemplater reactEmailTemplater,
            final SimpleRedisProvider simpleRedisProvider,
            final AuthSessionService authSessionService,
            final PasswordEncoder passwordEncoder) {
        this.sessionRepository = sessionRepository;
        this.protector = protector;
        this.userRepository = userRepository;
        this.jwtClient = jwtClient;
        this.emailClient = emailClient;
        this.serverUrlUtils = serverUrlUtils;
        this.userTagRepository = userTagRepository;
        this.reporter = reporter;
        this.reactEmailTemplater = reactEmailTemplater;
        this.simpleRedis = simpleRedisProvider.select(SimpleRedisSlot.VERIFICATION_EMAIL_SENDING);
        this.authSessionService = authSessionService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(
            summary = "Validate if the user is authenticated or not.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Authenticated"),
                @ApiResponse(
                        responseCode = "401",
                        description = "Not authenticated",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            })
    @GetMapping("/validate")
    public ResponseEntity<ApiResponder<AuthenticationObjectDto>> validateAuth(
            @Protected final AuthenticationObject authenticationObject) {
        FakeLag.sleep(350);

        return ResponseEntity.ok()
                .body(ApiResponder.success(
                        "You are authenticated!",
                        AuthenticationObjectDto.fromAuthenticationObject(authenticationObject)));
    }

    @Operation(
            summary = "Register a new account with email + password",
            responses = {
                @ApiResponse(responseCode = "200", description = "Registered and logged in successfully"),
                @ApiResponse(
                        responseCode = "409",
                        description = "Email already in use",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            })
    @PostMapping("/register")
    public ResponseEntity<ApiResponder<UserDto>> register(
            @Valid @RequestBody final RegisterBody registerBody, final HttpServletResponse response) {
        String email = registerBody.getEmail().trim().toLowerCase();

        if (userRepository.getUserByEmail(email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists.");
        }

        boolean grantAdmin = registerBody.isBecomeAdmin() && !userRepository.anyAdminExists();

        User newUser = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(registerBody.getPassword()))
                .nickname(registerBody.getNickname())
                .admin(grantAdmin)
                .build();
        userRepository.createUser(newUser);
        authSessionService.addUserToCurrentLeaderboard(newUser);

        authSessionService.createSessionAndSetCookie(response, newUser);

        return ResponseEntity.ok()
                .body(ApiResponder.success("Account created successfully!", UserDto.fromUser(newUser)));
    }

    @Operation(
            summary = "Check whether the one-time 'become admin at sign-up' option is still available",
            responses = {
                @ApiResponse(responseCode = "200", description = "True if no admin account exists yet")
            })
    @GetMapping("/admin-bootstrap-available")
    public ResponseEntity<ApiResponder<Boolean>> adminBootstrapAvailable() {
        boolean available = !userRepository.anyAdminExists();
        return ResponseEntity.ok().body(ApiResponder.success("OK", available));
    }

    @Operation(
            summary = "Log in with email + password",
            responses = {
                @ApiResponse(responseCode = "200", description = "Logged in successfully"),
                @ApiResponse(
                        responseCode = "401",
                        description = "Invalid credentials",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            })
    @PostMapping("/login")
    public ResponseEntity<ApiResponder<UserDto>> login(
            @Valid @RequestBody final LoginBody loginBody, final HttpServletResponse response) {
        String email = loginBody.getEmail().trim().toLowerCase();

        User user = userRepository.getUserByEmail(email);

        if (user == null
                || user.getPasswordHash() == null
                || !passwordEncoder.matches(loginBody.getPassword(), user.getPasswordHash())) {
            // Intentionally generic: do not leak whether the email exists.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }

        authSessionService.createSessionAndSetCookie(response, user);

        return ResponseEntity.ok().body(ApiResponder.success("Logged in successfully!", UserDto.fromUser(user)));
    }

    // Decided to make this redirect to routes, with a message query if needed,
    // keeping it inline with the logic of the authentication handler.
    @Operation(
            summary = "Logs user out",
            description =
                    "Logs the user out if currently authenticated. This is a Redirect route that does redirects as responses.",
            responses = {
                @ApiResponse(
                        responseCode = "302",
                        description =
                                "Redirect to `/login?success=true&message=\"Successful logout message here.\"` on successful authentication.",
                        content = @Content),
            })
    @GetMapping("/logout")
    public RedirectView logout(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            AuthenticationObject authenticationObject = protector.validateSession(request);

            Session session = authenticationObject.getSession();

            String sessionId =
                    session.getId().orElseThrow(() -> new IllegalStateException("Authenticated session has no id"));
            boolean sessionDeleted = sessionRepository.deleteSessionById(sessionId);

            if (!sessionDeleted) {
                return new RedirectView("/login?success=false&message=You are not logged in.");
            }

            ResponseCookie strippedCookie = ResponseCookie.from("session_token", "")
                    .path("/")
                    .httpOnly(true)
                    .maxAge(0)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, strippedCookie.toString());

            return new RedirectView("/login?success=true&message=You have been logged out!");
        } catch (Exception e) {
            return new RedirectView("/login?success=false&message=You are not logged in.");
        }
    }

    @Operation(
            summary = "Logs user out from all sessions",
            description =
                    "Logs the user out from all authenticated sessions across all devices. This is a Redirect route that does redirects as responses.",
            responses = {
                @ApiResponse(
                        responseCode = "302",
                        description =
                                "Redirect to `/login?success=true&message=\"Successful logout message here.\"` on successful authentication.",
                        content = @Content),
            })
    @GetMapping("/logout/all")
    public RedirectView logoutAll(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            AuthenticationObject authenticationObject = protector.validateSession(request);

            String userId = authenticationObject.getUser().getId();

            sessionRepository.deleteSessionsByUserId(userId);

            ResponseCookie strippedCookie = ResponseCookie.from("session_token", "")
                    .path("/")
                    .httpOnly(true)
                    .maxAge(0)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, strippedCookie.toString());

            return new RedirectView("/login?success=true&message=You have been logged out from all devices!");
        } catch (Exception e) {
            return new RedirectView("/login?success=false&message=You are not logged in.");
        }
    }

    @Operation(
            summary = "Enroll with a school email (if supported)",
            description = """
            Allows users to submit a school-specific email if supported. Emails will be verified with a magic link sent to their email.
                """,
            responses = {
                @ApiResponse(responseCode = "200", description = "email send successfully"),
                @ApiResponse(responseCode = "500", description = "not implemented"),
            })
    @PostMapping("/school/enroll")
    public ResponseEntity<ApiResponder<Empty>> enrollSchool(
            @Valid @RequestBody final EmailBody emailBody, @Protected final AuthenticationObject authenticationObject) {
        User user = authenticationObject.getUser();
        String userId = user.getId();

        String email = emailBody.getEmail();
        String domain = email.substring(email.indexOf("@")).toLowerCase();
        Set<String> supportedDomains = Stream.of(SchoolEnum.values())
                .map(school -> school.getEmailDomain())
                .collect(Collectors.toSet());

        if (!supportedDomains.contains(domain)) {
            String supportedSchools = String.join(", ", supportedDomains);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The email is not part of our supported schools domains: " + supportedSchools);
        }

        if (simpleRedis.containsKey(userId)) {
            long timeThen = simpleRedis.get(userId);
            long timeNow = System.currentTimeMillis();
            long difference = (timeNow - timeThen) / 1000;

            if (difference < SECONDS_TO_WAIT) {
                long remainingTime = (long) SECONDS_TO_WAIT - difference;
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Please try again in " + Long.toString(remainingTime) + " seconds.");
            }
        }

        simpleRedis.put(userId, System.currentTimeMillis());

        MagicLink magicLink = new MagicLink(email, userId);
        try {
            String token = jwtClient.encode(magicLink, Duration.ofHours(1));
            String verificationLink = serverUrlUtils.getUrl() + "/api/auth/school/verify?state=" + token;
            emailClient.sendMessage(SendEmailOptions.builder()
                    .recipientEmail(email)
                    .subject("Hello from CodePulse!")
                    .body(reactEmailTemplater.schoolEmailTemplate(verificationLink))
                    .build());
            return ResponseEntity.ok()
                    .body(ApiResponder.success("Magic link sent! Check your school inbox to continue.", Empty.of()));
        } catch (EmailException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error processing request: not implemented");
        }
    }

    @Operation(
            summary = "Verifies the JWT",
            description =
                    "Verifies the magic link sent to the user's email. If successful, the user will be enrolled with the school tag.",
            responses = {
                @ApiResponse(responseCode = "302", description = "Redirect to /settings with success or error message"),
            })
    @GetMapping("/school/verify")
    public RedirectView verifySchoolEmail(final HttpServletRequest request) {
        AuthenticationObject authenticationObject;
        Session session;
        User user;
        try {
            authenticationObject = protector.validateSession(request);
            session = authenticationObject.getSession();
            user = authenticationObject.getUser();
        } catch (Exception e) {
            return new RedirectView("/login?success=false&message=You are not logged in.");
        }
        if (session == null) {
            return new RedirectView("/settings?success=false&message=You are not logged in");
        }

        String token = request.getParameter("state");
        MagicLink magicLink;
        try {
            magicLink = jwtClient.decode(token, MagicLink.class);
        } catch (Exception e) {
            return new RedirectView("/settings?success=false&message=Invalid or expired token");
        }

        String magicLinkId = magicLink.getUserId();
        String currentUserId = authenticationObject.getUser().getId();
        if (!magicLinkId.equals(currentUserId)) {
            return new RedirectView("/settings?success=false&message=ID does not match current user");
        }

        user.setSchoolEmail(magicLink.getEmail());
        boolean isSuccessful = userRepository.updateUser(user);

        if (!isSuccessful) {
            throw new RuntimeException("User repository failed to update user and add school email.");
        }

        String emailDomain = magicLink
                .getEmail()
                .substring(magicLink.getEmail().indexOf("@"))
                .toLowerCase();

        SchoolEnum schoolEnum = Stream.of(SchoolEnum.values())
                .filter(school -> school.getEmailDomain().equals(emailDomain))
                .findFirst()
                .orElse(null);
        if (schoolEnum == null) {
            return new RedirectView("/settings?success=false&message=This email is not supported");
        }

        UserTag schoolTag = UserTag.builder()
                .userId(user.getId())
                .tag(schoolEnum.getInternalTag())
                .build();

        if (user.getTags().stream()
                .anyMatch(tag ->
                        tag.getTag().name().equals(schoolEnum.getInternalTag().name()))) {
            reporter.log(
                    "auth",
                    Report.builder()
                            .data(String.format(
                                    "User %s\nAlready has tag %s",
                                    user.getNickname() != null ? user.getNickname() : user.getDiscordName(),
                                    schoolEnum.getInternalTag().name()))
                            .build());
        } else {
            userTagRepository.createTag(schoolTag);
        }

        return new RedirectView("/settings?success=true&message=The email has been verified!");
    }

    @Operation(
            summary = "Request a password reset link",
            description =
                    """
            Looks up the account by email and, if found, emails a signed, short-lived reset link
            (reusing the same signed-token pattern as the school-email magic link flow). Always
            returns the same generic success response so callers cannot enumerate registered emails.
            This is also how pre-existing accounts with no password set (previously Discord-only
            accounts) claim a password for the first time.
                """,
            responses = {
                @ApiResponse(responseCode = "200", description = "If an account exists, a reset link was emailed"),
            })
    @PostMapping("/password-reset/request")
    public ResponseEntity<ApiResponder<Empty>> requestPasswordReset(
            @Valid @RequestBody final PasswordResetRequestBody passwordResetRequestBody) {
        String email = passwordResetRequestBody.getEmail().trim().toLowerCase();
        String genericMessage = "If an account with that email exists, a password reset link has been sent.";

        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            // Intentionally generic: do not leak whether the email exists.
            return ResponseEntity.ok().body(ApiResponder.success(genericMessage, Empty.of()));
        }

        PasswordResetToken passwordResetToken = new PasswordResetToken(user.getId());
        try {
            String token = jwtClient.encode(passwordResetToken, Duration.ofMinutes(30));
            String resetLink = serverUrlUtils.getUrl() + "/reset-password?token=" + token;
            emailClient.sendMessage(SendEmailOptions.builder()
                    .recipientEmail(email)
                    .subject("Reset your CodePulse password")
                    .body(reactEmailTemplater.passwordResetEmailTemplate(resetLink))
                    .build());
        } catch (EmailException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request.");
        }

        return ResponseEntity.ok().body(ApiResponder.success(genericMessage, Empty.of()));
    }

    @Operation(
            summary = "Confirm a password reset with a token",
            description =
                    """
            Verifies the signed reset token and sets a new password on the associated account. This
            also doubles as the "claim a legacy account" flow for pre-existing users whose
            passwordHash is null.
                """,
            responses = {
                @ApiResponse(responseCode = "200", description = "Password updated successfully"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid or expired token",
                        content = @Content(schema = @Schema(implementation = UnsafeGenericFailureResponse.class))),
            })
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<ApiResponder<Empty>> confirmPasswordReset(
            @Valid @RequestBody final PasswordResetConfirmBody passwordResetConfirmBody) {
        PasswordResetToken passwordResetToken;
        try {
            passwordResetToken = jwtClient.decode(passwordResetConfirmBody.getToken(), PasswordResetToken.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token.");
        }

        if (passwordResetToken == null || passwordResetToken.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token.");
        }

        User user = userRepository.getUserById(passwordResetToken.getUserId());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token.");
        }

        user.setPasswordHash(passwordEncoder.encode(passwordResetConfirmBody.getNewPassword()));
        boolean isSuccessful = userRepository.updateUser(user);

        if (!isSuccessful) {
            throw new RuntimeException("User repository failed to update user's password.");
        }

        sessionRepository.deleteSessionsByUserId(user.getId());

        return ResponseEntity.ok().body(ApiResponder.success("Your password has been updated!", Empty.of()));
    }
}
