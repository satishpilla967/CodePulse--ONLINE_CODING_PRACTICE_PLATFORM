package org.patinanetwork.codepulse.common.dto.security;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codepulse.common.dto.session.SessionDto;
import org.patinanetwork.codepulse.common.dto.user.PrivateUserDto;
import org.patinanetwork.codepulse.common.security.AuthenticationObject;

@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class AuthenticationObjectDto {

    private PrivateUserDto user;
    private SessionDto session;

    public static AuthenticationObjectDto fromAuthenticationObject(final AuthenticationObject authenticationObject) {
        return AuthenticationObjectDto.builder()
                // ok to send private user, only ever sending auth object down the wire when
                // validating authenticated user.
                .user(PrivateUserDto.fromUser(authenticationObject.getUser()))
                .session(SessionDto.fromSession(authenticationObject.getSession()))
                .build();
    }
}
