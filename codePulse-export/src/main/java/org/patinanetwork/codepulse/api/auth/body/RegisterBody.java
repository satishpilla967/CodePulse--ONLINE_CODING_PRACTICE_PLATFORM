package org.patinanetwork.codepulse.api.auth.body;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterBody {

    @Email
    @Size(min = 1, max = 320)
    @NotBlank
    private String email;

    @Size(min = 8, max = 72)
    @NotBlank
    private String password;

    @Size(max = 255)
    private String nickname;

    /**
     * One-time bootstrap opt-in: if true and no admin account exists yet anywhere in the system,
     * this new account is granted admin. Ignored once any admin already exists.
     */
    private boolean becomeAdmin;

    public RegisterBody(final String email, final String password, final String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public boolean isBecomeAdmin() {
        return becomeAdmin;
    }

    public void setBecomeAdmin(final boolean becomeAdmin) {
        this.becomeAdmin = becomeAdmin;
    }
}
