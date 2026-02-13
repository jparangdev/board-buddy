package kr.co.jparangdev.boardbuddy.application.auth.dto;

import kr.co.jparangdev.boardbuddy.domain.auth.ProviderType;

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
