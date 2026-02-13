package kr.co.jparangdev.boardbuddy.application.auth.service;

import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthCredentials;
import kr.co.jparangdev.boardbuddy.domain.auth.ProviderType;
import kr.co.jparangdev.boardbuddy.domain.user.User;

/**
 * Authentication provider interface for extensible authentication system.
 * Implement this interface to add new authentication methods (OAuth, test, etc.)
 */
public interface AuthenticationProvider {

    /**
     * Get the provider type (e.g., TEST, KAKAO)
     */
    ProviderType getProviderType();

    /**
     * Authenticate user with given credentials and return User
     * @param credentials Provider-specific credentials
     * @return Authenticated user
     */
    User authenticate(AuthCredentials credentials);

    /**
     * Check if this provider supports the given provider type
     */
    default boolean supports(ProviderType providerType) {
        return getProviderType() == providerType;
    }
}
