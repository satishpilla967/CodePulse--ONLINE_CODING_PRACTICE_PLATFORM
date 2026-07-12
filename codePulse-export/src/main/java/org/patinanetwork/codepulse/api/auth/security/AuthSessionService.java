package org.patinanetwork.codepulse.api.auth.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import org.patinanetwork.codepulse.common.db.models.Session;
import org.patinanetwork.codepulse.common.db.models.user.User;
import org.patinanetwork.codepulse.common.db.repos.leaderboard.LeaderboardRepository;
import org.patinanetwork.codepulse.common.db.repos.session.SessionRepository;
import org.patinanetwork.codepulse.common.time.StandardizedLocalDateTime;
import org.springframework.stereotype.Component;

/**
 * Shared post-authentication logic used by both {@code /api/auth/register} and {@code /api/auth/login}. Previously
 * this logic lived in {@code CustomAuthenticationSuccessHandler}, which was invoked after a successful Discord OAuth2
 * login. Now that login is email/password based, this is invoked directly by {@code AuthController}.
 *
 * @see <a href= "https://github.com/tahminator/codepulse/tree/main/docs/auth.md">Authentication Documentation</a>
 */
@Component
public class AuthSessionService {

    // 30 days expiration
    public static final int MAX_AGE_SECONDS = 60 * 60 * 24 * 30;

    private final SessionRepository sessionRepository;
    private final LeaderboardRepository leaderboardRepository;

    public AuthSessionService(
            final SessionRepository sessionRepository, final LeaderboardRepository leaderboardRepository) {
        this.sessionRepository = sessionRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    /** Adds a newly-created user to the currently active leaderboard, if one exists. */
    public void addUserToCurrentLeaderboard(final User user) {
        leaderboardRepository
                .getRecentLeaderboardMetadata()
                .ifPresent(lb -> leaderboardRepository.addUserToLeaderboard(user.getId(), lb.getId()));
    }

    /** Creates a new 30-day session for the given user. */
    public Session createSession(final User user) {
        LocalDateTime expirationTime = StandardizedLocalDateTime.now().plusSeconds(MAX_AGE_SECONDS);

        Session session = Session.builder()
                .userId(user.getId())
                .expiresAt(expirationTime)
                .build();
        sessionRepository.createSession(session);

        if (session.getId().isEmpty()) {
            throw new RuntimeException("Failed to create new session.");
        }

        return session;
    }

    /** Sets the {@code session_token} cookie on the response for the given session. */
    public void setSessionCookie(final HttpServletResponse response, final Session session) {
        Cookie cookie = new Cookie(
                "session_token", session.getId().orElseThrow(() -> new IllegalStateException("Session has no id")));
        cookie.setMaxAge(MAX_AGE_SECONDS);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    /** Convenience method combining {@link #createSession} and {@link #setSessionCookie}. */
    public Session createSessionAndSetCookie(final HttpServletResponse response, final User user) {
        Session session = createSession(user);
        setSessionCookie(response, session);
        return session;
    }
}
