package kr.co.jparangdev.boardbuddy.application.auth.dto;

import kr.co.jparangdev.boardbuddy.domain.auth.ProviderType;

public record LocalAuthCredentials(String email, String rawPassword) implements AuthCredentials {

    @Override
    public ProviderType getProviderType() {
        return ProviderType.LOCAL;
    }
}
