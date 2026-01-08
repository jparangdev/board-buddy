package kr.co.jparangdev.boardbuddy.application.auth;

public interface AuthenticationUseCase {
    /**
     * Generate OAuth authorization URL for Naver login
     */
    String getNaverAuthorizationUrl();

    /**
     * Process OAuth callback and register/login user
     * Returns JWT tokens
     */
    AuthTokens processNaverCallback(String code, String state);

    /**
     * Refresh access token using refresh token
     */
    AuthTokens refreshAccessToken(String refreshToken);

    /**
     * Logout - invalidate refresh token
     */
    void logout(String refreshToken);
}
