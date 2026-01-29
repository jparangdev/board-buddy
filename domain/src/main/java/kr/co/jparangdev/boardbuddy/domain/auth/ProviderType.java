package kr.co.jparangdev.boardbuddy.domain.auth;

/**
 * Supported authentication provider types.
 * Add new providers here when implementing additional OAuth integrations.
 */
public enum ProviderType {
    TEST,   // For development and testing
    KAKAO   // Kakao OAuth (to be implemented)
}
