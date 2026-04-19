package kr.co.jparangdev.boardbuddy.application.auth.service;

import kr.co.jparangdev.boardbuddy.application.auth.dto.OAuthUserInfo;
import kr.co.jparangdev.boardbuddy.domain.auth.ProviderType;

public interface OAuthProvider {

    ProviderType getProviderType();

    String buildAuthorizationUrl(String redirectUri);

    OAuthUserInfo fetchUserInfo(String code, String redirectUri);
}
