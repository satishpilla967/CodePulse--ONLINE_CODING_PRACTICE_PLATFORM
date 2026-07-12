package org.patinanetwork.codepulse.api.auth.body;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordResetConfirmBody {

    @NotBlank
    private String token;

    @Size(min = 8, max = 72)
    @NotBlank
    private String newPassword;

    public PasswordResetConfirmBody(final String token, final String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }
}
