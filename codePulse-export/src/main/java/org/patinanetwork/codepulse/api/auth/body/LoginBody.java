package org.patinanetwork.codepulse.api.auth.body;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginBody {

    @Email
    @Size(min = 1, max = 320)
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 1, max = 72)
    private String password;

    public LoginBody(final String email, final String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
