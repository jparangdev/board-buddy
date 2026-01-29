package kr.co.jparangdev.boardbuddy.application.auth.dto;

import kr.co.jparangdev.boardbuddy.domain.auth.ProviderType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Credentials for test authentication.
 * Used for development and testing purposes.
 */
@Getter
@RequiredArgsConstructor
public class TestAuthCredentials implements AuthCredentials {

    private final String email;
    private final String nickname;

    @Override
    public ProviderType getProviderType() {
        return ProviderType.TEST;
    }
}
