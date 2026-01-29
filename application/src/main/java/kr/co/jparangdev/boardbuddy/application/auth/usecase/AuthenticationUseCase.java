package kr.co.jparangdev.boardbuddy.application.auth.usecase;

import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthCredentials;
import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthTokens;

public interface AuthenticationUseCase {

    /**
     * Authenticate user with given credentials and return JWT tokens.
     * Supports multiple authentication providers (LOCAL, NAVER, KAKAO, etc.)
     *
     * @param credentials Provider-specific credentials
     * @return JWT tokens (access token, refresh token)
     */
    AuthTokens authenticate(AuthCredentials credentials);

    /**
     * Refresh access token using refresh token
     */
    AuthTokens refreshAccessToken(String refreshToken);

    /**
     * Logout - invalidate refresh token
     */
    void logout(String refreshToken);
}
