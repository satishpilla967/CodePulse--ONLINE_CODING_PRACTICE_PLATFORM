package org.patinanetwork.codepulse.api.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.patinanetwork.codepulse.api.auth.body.EmailBody;
import org.patinanetwork.codepulse.api.auth.body.PasswordResetConfirmBody;
import org.patinanetwork.codepulse.api.auth.body.PasswordResetRequestBody;
import org.patinanetwork.codepulse.api.auth.security.AuthSessionService;
import org.patinanetwork.codepulse.common.db.models.Session;
import org.patinanetwork.codepulse.common.db.models.user.User;
import org.patinanetwork.codepulse.common.db.repos.session.SessionRepository;
import org.patinanetwork.codepulse.common.db.repos.user.UserRepository;
import org.patinanetwork.codepulse.common.db.repos.usertag.UserTagRepository;
import org.patinanetwork.codepulse.common.email.client.codepulse.OfficialCodePulseEmailClient;
import org.patinanetwork.codepulse.common.email.error.EmailException;
import org.patinanetwork.codepulse.common.email.options.SendEmailOptions;
import org.patinanetwork.codepulse.common.email.template.ReactEmailTemplater;
import org.patinanetwork.codepulse.common.jwt.JWTClient;
import org.patinanetwork.codepulse.common.reporter.Reporter;
import org.patinanetwork.codepulse.common.schools.magic.MagicLink;
import org.patinanetwork.codepulse.common.security.AuthenticationObject;
import org.patinanetwork.codepulse.common.security.Protector;
import org.patinanetwork.codepulse.common.security.passwordreset.PasswordResetToken;
import org.patinanetwork.codepulse.common.simpleredis.SimpleRedis;
import org.patinanetwork.codepulse.common.simpleredis.SimpleRedisProvider;
import org.patinanetwork.codepulse.common.simpleredis.SimpleRedisSlot;
import org.patinanetwork.codepulse.common.url.ServerUrlUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

public class AuthControllerTest {

    private final SessionRepository sessionRepository = mock(SessionRepository.class);
    private final Protector protector = mock(Protector.class);
    private final JWTClient jwtClient = mock(JWTClient.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OfficialCodePulseEmailClient emailClient = mock(OfficialCodePulseEmailClient.class);
    private final ServerUrlUtils serverUrlUtils = mock(ServerUrlUtils.class);
    private final UserTagRepository userTagRepository = mock(UserTagRepository.class);
    private final Reporter reporter = mock(Reporter.class);
    private final ReactEmailTemplater reactEmailTemplater = mock(ReactEmailTemplater.class);
    private final SimpleRedis<Long> simpleRedis = mock(SimpleRedis.class);
    private final SimpleRedisProvider simpleRedisProvider = mock(SimpleRedisProvider.class);
    private final AuthSessionService authSessionService = mock(AuthSessionService.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    private AuthController authController;
    private Faker faker;

    @BeforeEach
    void setup() {
        when(simpleRedisProvider.select(SimpleRedisSlot.VERIFICATION_EMAIL_SENDING))
                .thenReturn(simpleRedis);
        this.authController = new AuthController(
                sessionRepository,
                protector,
                jwtClient,
                userRepository,
                emailClient,
                serverUrlUtils,
                userTagRepository,
                reporter,
                reactEmailTemplater,
                simpleRedisProvider,
                authSessionService,
                passwordEncoder);
        this.faker = Faker.instance();
    }

    private String randomUUID() {
        return UUID.randomUUID().toString();
    }

    private User createRandomUser() {
        return User.builder()
                .id(randomUUID())
                .discordId(String.valueOf(faker.number().randomNumber(18, true)))
                .discordName(faker.name().username())
                .leetcodeUsername(faker.name().username())
                .admin(false)
                .verifyKey(faker.crypto().md5())
                .build();
    }

    private Session createRandomSession(final String userId) {
        return Session.builder()
                .id(Optional.of(UUID.randomUUID().toString().replace("-", "")))
                .userId(userId)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    private AuthenticationObject createAuthenticationObject(final User user, final Session session) {
        return new AuthenticationObject(user, session);
    }

    @Test
    @DisplayName("Validate auth - happy path")
    void validateAuthHappyPath() {
        User user = createRandomUser();
        Session session = createRandomSession(user.getId());
        AuthenticationObject authObj = createAuthenticationObject(user, session);

        var response = authController.validateAuth(authObj);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        var apiResponder = response.getBody();
        assertNotNull(apiResponder);
        assertTrue(apiResponder.isSuccess());
        assertEquals("You are authenticated!", apiResponder.getMessage());
        assertNotNull(apiResponder.getPayload());
    }

    @Test
    @DisplayName("Logout - successful logout")
    void logoutHappyPath() {
        User user = createRandomUser();
        Session session = createRandomSession(user.getId());
        AuthenticationObject authObj = createAuthenticationObject(user, session);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(protector.validateSession(request)).thenReturn(authObj);
        when(sessionRepository.deleteSessionById(session.getId().orElseThrow())).thenReturn(true);

        RedirectView redirectView = authController.logout(request, response);

        assertNotNull(redirectView);
        assertEquals("/login?success=true&message=You have been logged out!", redirectView.getUrl());

        verify(protector, times(1)).validateSession(request);
        verify(sessionRepository, times(1)).deleteSessionById(session.getId().orElseThrow());
    }

    @Test
    @DisplayName("Logout - session not found")
    void logoutSessionNotFound() {
        User user = createRandomUser();
        Session session = createRandomSession(user.getId());
        AuthenticationObject authObj = createAuthenticationObject(user, session);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(protector.validateSession(request)).thenReturn(authObj);
        when(sessionRepository.deleteSessionById(session.getId().orElseThrow())).thenReturn(false);

        RedirectView redirectView = authController.logout(request, response);

        assertNotNull(redirectView);
        assertEquals("/login?success=false&message=You are not logged in.", redirectView.getUrl());

        verify(protector, times(1)).validateSession(request);
        verify(sessionRepository, times(1)).deleteSessionById(session.getId().orElseThrow());
    }

    @Test
    @DisplayName("Logout - not authenticated")
    void logoutNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(protector.validateSession(request)).thenThrow(new RuntimeException("Not authenticated"));

        RedirectView redirectView = authController.logout(request, response);

        assertNotNull(redirectView);
        assertEquals("/login?success=false&message=You are not logged in.", redirectView.getUrl());

        verify(protector, times(1)).validateSession(request);
        verify(sessionRepository, times(0)).deleteSessionById(any());
    }

    @Test
    @DisplayName("Logout all - successful logout from all devices")
    void logoutAllHappyPath() {
        User user = createRandomUser();
        Session session = createRandomSession(user.getId());
        AuthenticationObject authObj = createAuthenticationObject(user, session);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(protector.validateSession(request)).thenReturn(authObj);
        when(sessionRepository.deleteSessionsByUserId(user.getId())).thenReturn(true);

        RedirectView redirectView = authController.logoutAll(request, response);

        assertNotNull(redirectView);
        assertEquals("/login?success=true&message=You have been logged out from all devices!", redirectView.getUrl());

        verify(protector, times(1)).validateSession(request);
        verify(sessionRepository, times(1)).deleteSessionsByUserId(user.getId());
    }

    @Test
    @DisplayName("Logout all - not authenticated")
    void logoutAllNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(protector.validateSession(request)).thenThrow(new RuntimeException("Not authenticated"));

        RedirectView redirectView = authController.logoutAll(request, response);

        assertNotNull(redirectView);
        assertEquals("/login?success=false&message=You are not logged in.", redirectView.getUrl());

        verify(protector, times(1)).validateSession(request);
        verify(sessionRepository, times(0)).deleteSessionsByUserId(any());
    }

    @Test
    @DisplayName("Enroll school - unsupported email domain")
    void enrollSchoolUnsupportedDomain() {
        User user = createRandomUser();
        Session session = createRandomSession(user.getId());
        AuthenticationObject authObj = createAuthenticationObject(user, session);

        EmailBody emailBody = new EmailBody("test@unsupported.com");

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> authController.enrollSchool(emailBody, authObj));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("not part of our supported schools domains"));
    }

    @Test
    @DisplayName("Enroll school - rate limited")
    void enrollSchoolRateLimited() {
        User user = createRandomUser();
        Session session = createRandomSession(user.getId());
        AuthenticationObject authObj = createAuthenticationObject(user, session);

        EmailBody emailBody = new EmailBody("test@myhunter.cuny.edu");

        when(simpleRedis.containsKey(user.getId())).thenReturn(true);
        when(simpleRedis.get(user.getId())).thenReturn(System.currentTimeMillis());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> authController.enrollSchool(emailBody, authObj));

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Please try again in"));

        verify(simpleRedis, times(1)).containsKey(user.getId());
    }

    @Test
    @DisplayName("Enroll school - email send failure")
    void enrollSchoolEmailSendFailure() throws Exception {
        User user = createRandomUser();
        Session session = createRandomSession(user.getId());
        AuthenticationObject authObj = createAuthenticationObject(user, session);

        EmailBody emailBody = new EmailBody("test@myhunter.cuny.edu");

        when(jwtClient.encode(any(MagicLink.class), any(Duration.class))).thenReturn("mock-token");
        when(serverUrlUtils.getUrl()).thenReturn("http://localhost:8080");
        when(reactEmailTemplater.schoolEmailTemplate(any())).thenReturn("<html>Template</html>");
        doThrow(new EmailException("Failed to send email")).when(emailClient).sendMessage(any(SendEmailOptions.class));

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> authController.enrollSchool(emailBody, authObj));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Failed to send email.", exception.getReason());

        verify(emailClient, times(1)).sendMessage(any(SendEmailOptions.class));
    }

    @Test
    @DisplayName("Enroll school - happy path")
    void enrollSchoolHappyPath() throws Exception {
        User user = createRandomUser();
        Session session = createRandomSession(user.getId());
        AuthenticationObject authObj = createAuthenticationObject(user, session);

        EmailBody emailBody = new EmailBody("test@myhunter.cuny.edu");

        when(simpleRedis.containsKey(user.getId())).thenReturn(false);
        when(jwtClient.encode(any(MagicLink.class), any(Duration.class))).thenReturn("mock-token");
        when(serverUrlUtils.getUrl()).thenReturn("http://localhost:8080");
        when(reactEmailTemplater.schoolEmailTemplate(any())).thenReturn("<html>Template</html>");

        var response = authController.enrollSchool(emailBody, authObj);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        var apiResponder = response.getBody();
        assertNotNull(apiResponder);
        assertTrue(apiResponder.isSuccess());
        assertEquals("Magic link sent! Check your school inbox to continue.", apiResponder.getMessage());

        verify(emailClient, times(1)).sendMessage(any(SendEmailOptions.class));
        verify(simpleRedis, times(1)).put(eq(user.getId()), any(Long.class));
    }

    @Test
    @DisplayName("Verify school email - not authenticated")
    void verifySchoolEmailNotAuthenticated() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(protector.validateSession(request)).thenThrow(new RuntimeException("Not authenticated"));

        RedirectView redirectView = authController.verifySchoolEmail(request);

        assertNotNull(redirectView);
        assertEquals("/login?success=false&message=You are not logged in.", redirectView.getUrl());

        verify(protector, times(1)).validateSession(request);
    }

    @Test
    @DisplayName("Verify school email - invalid token")
    void verifySchoolEmailInvalidToken() throws Exception {
        User user = createRandomUser();
        Session session = createRandomSession(user.getId());
        AuthenticationObject authObj = createAuthenticationObject(user, session);

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(protector.validateSession(request)).thenReturn(authObj);
        when(request.getParameter("state")).thenReturn("invalid-token");
        when(jwtClient.decode("invalid-token", MagicLink.class)).thenThrow(new RuntimeException("Invalid token"));

        RedirectView redirectView = authController.verifySchoolEmail(request);

        assertNotNull(redirectView);
        assertEquals("/settings?success=false&message=Invalid or expired token", redirectView.getUrl());

        verify(protector, times(1)).validateSession(request);
        verify(jwtClient, times(1)).decode("invalid-token", MagicLink.class);
    }

    @Test
    @DisplayName("Verify school email - user ID mismatch")
    void verifySchoolEmailUserIdMismatch() throws Exception {
        User user = createRandomUser();
        Session session = createRandomSession(user.getId());
        AuthenticationObject authObj = createAuthenticationObject(user, session);

        HttpServletRequest request = mock(HttpServletRequest.class);
        MagicLink magicLink = new MagicLink("test@myhunter.cuny.edu", "different-user-id");

        when(protector.validateSession(request)).thenReturn(authObj);
        when(request.getParameter("state")).thenReturn("valid-token");
        when(jwtClient.decode("valid-token", MagicLink.class)).thenReturn(magicLink);

        RedirectView redirectView = authController.verifySchoolEmail(request);

        assertNotNull(redirectView);
        assertEquals("/settings?success=false&message=ID does not match current user", redirectView.getUrl());

        verify(protector, times(1)).validateSession(request);
        verify(jwtClient, times(1)).decode("valid-token", MagicLink.class);
    }

    @Test
    @DisplayName("Verify school email - happy path")
    void verifySchoolEmailHappyPath() throws Exception {
        User user = createRandomUser();
        Session session = createRandomSession(user.getId());
        AuthenticationObject authObj = createAuthenticationObject(user, session);

        HttpServletRequest request = mock(HttpServletRequest.class);
        MagicLink magicLink = new MagicLink("test@myhunter.cuny.edu", user.getId());

        when(protector.validateSession(request)).thenReturn(authObj);
        when(request.getParameter("state")).thenReturn("valid-token");
        when(jwtClient.decode("valid-token", MagicLink.class)).thenReturn(magicLink);
        when(userRepository.updateUser(any(User.class))).thenReturn(true);

        RedirectView redirectView = authController.verifySchoolEmail(request);

        assertNotNull(redirectView);
        assertEquals("/settings?success=true&message=The email has been verified!", redirectView.getUrl());

        verify(protector, times(1)).validateSession(request);
        verify(jwtClient, times(1)).decode("valid-token", MagicLink.class);
        verify(userRepository, times(1)).updateUser(any(User.class));
        verify(userTagRepository, times(1)).createTag(any());
    }

    @Test
    @DisplayName("Password reset request - email not found returns generic success")
    void passwordResetRequestEmailNotFound() throws Exception {
        PasswordResetRequestBody body = new PasswordResetRequestBody("nobody@example.com");

        when(userRepository.getUserByEmail("nobody@example.com")).thenReturn(null);

        var response = authController.requestPasswordReset(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        var apiResponder = response.getBody();
        assertNotNull(apiResponder);
        assertTrue(apiResponder.isSuccess());
        assertEquals(
                "If an account with that email exists, a password reset link has been sent.",
                apiResponder.getMessage());

        verify(emailClient, times(0)).sendMessage(any());
    }

    @Test
    @DisplayName("Password reset request - happy path")
    void passwordResetRequestHappyPath() throws Exception {
        User user = createRandomUser();
        PasswordResetRequestBody body = new PasswordResetRequestBody("test@example.com");

        when(userRepository.getUserByEmail("test@example.com")).thenReturn(user);
        when(jwtClient.encode(any(PasswordResetToken.class), any(Duration.class))).thenReturn("mock-token");
        when(serverUrlUtils.getUrl()).thenReturn("http://localhost:8080");
        when(reactEmailTemplater.passwordResetEmailTemplate(any())).thenReturn("<html>Template</html>");

        var response = authController.requestPasswordReset(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        var apiResponder = response.getBody();
        assertNotNull(apiResponder);
        assertTrue(apiResponder.isSuccess());
        assertEquals(
                "If an account with that email exists, a password reset link has been sent.",
                apiResponder.getMessage());

        verify(emailClient, times(1)).sendMessage(any(SendEmailOptions.class));
    }

    @Test
    @DisplayName("Password reset confirm - invalid or expired token")
    void passwordResetConfirmInvalidToken() throws Exception {
        PasswordResetConfirmBody body = new PasswordResetConfirmBody("bad-token", "newPassword123");

        when(jwtClient.decode("bad-token", PasswordResetToken.class)).thenThrow(new RuntimeException("bad token"));

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> authController.confirmPasswordReset(body));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(userRepository, times(0)).updateUser(any());
    }

    @Test
    @DisplayName("Password reset confirm - happy path, including legacy accounts with no password set")
    void passwordResetConfirmHappyPath() throws Exception {
        User user = createRandomUser();
        user.setPasswordHash(null);
        PasswordResetConfirmBody body = new PasswordResetConfirmBody("valid-token", "newPassword123");
        PasswordResetToken token = new PasswordResetToken(user.getId());

        when(jwtClient.decode("valid-token", PasswordResetToken.class)).thenReturn(token);
        when(userRepository.getUserById(user.getId())).thenReturn(user);
        when(passwordEncoder.encode("newPassword123")).thenReturn("hashed-password");
        when(userRepository.updateUser(any(User.class))).thenReturn(true);

        var response = authController.confirmPasswordReset(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        var apiResponder = response.getBody();
        assertNotNull(apiResponder);
        assertTrue(apiResponder.isSuccess());
        assertEquals("Your password has been updated!", apiResponder.getMessage());

        assertEquals("hashed-password", user.getPasswordHash());
        verify(userRepository, times(1)).updateUser(user);
        verify(sessionRepository, times(1)).deleteSessionsByUserId(user.getId());
    }
}
