package org.patinanetwork.codepulse.api.auth.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security filter chains for the application. Login is handled via email + password
 * ({@code /api/auth/register}, {@code /api/auth/login}) in {@code AuthController}, not via Spring's OAuth2 login
 * support. All application routes are permitAll() at the filter-chain level; real authorization happens downstream
 * via {@code @Protected}/{@code Protector}.
 *
 * @see <a href= "https://github.com/tahminator/codepulse/tree/main/docs/auth.md">Authentication Documentation</a>
 */
@Configuration
@EnableConfigurationProperties(SecurityActuatorProperties.class)
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(SecurityActuatorProperties props) {
        return new InMemoryUserDetailsManager(User.withUsername(props.username())
                .password("{noop}" + props.password())
                .roles("ACTUATOR")
                .build());
    }

    /**
     * Security filter chain for actuator endpoints with HTTP Basic authentication. This needs to be processed first
     * (Order 1) to prevent OAuth from being applied.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(final HttpSecurity http) throws Exception {
        http.securityMatcher("/actuator/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("ACTUATOR"))
                .httpBasic(basic -> {});

        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain embedSecurity(HttpSecurity http) throws Exception {
        http.securityMatcher("/embed/potd", "/embed/leaderboard")
                .headers(headers -> headers.frameOptions(f -> f.sameOrigin())
                        .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors *")))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    /**
     * Catch-all security filter chain for all other application routes, including the email/password
     * register/login endpoints. Authorization is enforced downstream via {@code @Protected}/{@code Protector}.
     */
    @Bean
    @Order(3)
    public SecurityFilterChain applicationSecurityFilterChain(final HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    // Remove the default login form that comes with SpringBoot Security.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/login");
    }
}
