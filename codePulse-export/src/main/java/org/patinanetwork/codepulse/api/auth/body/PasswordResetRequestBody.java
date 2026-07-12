package org.patinanetwork.codepulse.api.auth.body;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordResetRequestBody {

    @Email
    @Size(min = 1, max = 320)
    @NotBlank
    private String email;

    public PasswordResetRequestBody(final String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
}
