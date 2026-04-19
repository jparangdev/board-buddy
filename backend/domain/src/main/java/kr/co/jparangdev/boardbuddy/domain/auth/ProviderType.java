package kr.co.jparangdev.boardbuddy.domain.auth;

/**
 * Supported authentication provider types.
 * Add new providers here when implementing additional OAuth integrations.
 */
public enum ProviderType {
    LOCAL,
    KAKAO,
    NAVER,
    GOOGLE
}
