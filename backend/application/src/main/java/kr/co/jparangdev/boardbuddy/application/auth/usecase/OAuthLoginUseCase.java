package kr.co.jparangdev.boardbuddy.application.auth.usecase;

import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthTokens;

public interface OAuthLoginUseCase {

    String getAuthorizationUrl(String provider, String redirectUri);

    AuthTokens loginWithOAuth(String provider, String code, String redirectUri);
}
