package org.patinanetwork.codepulse.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.patinanetwork.codepulse.common.db.models.Session;
import org.patinanetwork.codepulse.common.db.models.user.User;
import org.patinanetwork.codepulse.common.db.repos.session.SessionRepository;
import org.patinanetwork.codepulse.common.db.repos.user.UserRepository;
import org.patinanetwork.codepulse.common.security.AuthenticationObject;
import org.patinanetwork.codepulse.common.security.Protector;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Use this if you need to override the admin behavior.
 *
 * <p>The returned user is an admin user, while the session attached to this user lasts until Jan 01, 2099, 12:59:50 PM
 * EST
 *
 * @see <a href=
 *     "https://github.com/tahminator/codepulse/tree/main/src/test/java/org/patinanetwork/codepulse/admin/AdminControllerTest.java">Example
 *     on how to use this test config</a>
 */
@TestConfiguration
public class TestProtector {

    private final UserRepository userRepository;
    private final SessionRepository sesssionRepository;

    public TestProtector(final UserRepository userRepository, final SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sesssionRepository = sessionRepository;
    }

    @Bean
    public Protector protector() {
        return new Protector(sesssionRepository, userRepository) {
            @Override
            public AuthenticationObject validateSession(final HttpServletRequest request) {
                User mockAdminUser = userRepository.getUserById("ed3bfe18-e42a-467f-b4fa-07e8da4d2555");
                Optional<Session> mockAdminSessionOp =
                        sesssionRepository.getSessionById("d99e10a2-6285-46f0-8150-ba4727b520f4");

                Session mockAdminSession = mockAdminSessionOp.isPresent() ? mockAdminSessionOp.get() : null;
                return new AuthenticationObject(mockAdminUser, mockAdminSession);
            }

            // User is an admin, so just send the same thing.
            @Override
            public AuthenticationObject validateAdminSession(final HttpServletRequest request) {
                return validateSession(request);
            }
        };
    }
}
