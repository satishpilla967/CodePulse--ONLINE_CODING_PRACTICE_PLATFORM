package org.patinanetwork.codepulse.common.security.passwordreset;

/**
 * Payload embedded in the signed JWT sent via the password-reset email link. Mirrors the shape/pattern of {@code
 * MagicLink} used by the school-email verification flow: a short-lived signed token carrying just enough identity
 * info to be verified server-side, with no separate persisted state (expiry is the replay-protection mechanism).
 */
public class PasswordResetToken {

    private String userId;

    public PasswordResetToken() {}

    public PasswordResetToken(final String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }
}
