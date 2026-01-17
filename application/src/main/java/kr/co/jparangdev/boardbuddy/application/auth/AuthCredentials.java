package kr.co.jparangdev.boardbuddy.application.auth;

/**
 * Marker interface for authentication credentials.
 * Each authentication provider can have its own credentials implementation.
 */
public interface AuthCredentials {
    /**
     * Get the provider type for these credentials
     */
    ProviderType getProviderType();
}
