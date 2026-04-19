package kr.co.jparangdev.boardbuddy.client.kakao;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.application.auth.dto.OAuthUserInfo;
import kr.co.jparangdev.boardbuddy.application.auth.service.OAuthProvider;
import kr.co.jparangdev.boardbuddy.domain.auth.ProviderType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoOAuthProvider implements OAuthProvider {

    private final KakaoOAuthClient kakaoOAuthClient;

    @Override
    public ProviderType getProviderType() {
        return ProviderType.KAKAO;
    }

    @Override
    public String buildAuthorizationUrl(String redirectUri) {
        return kakaoOAuthClient.buildAuthorizationUrl(redirectUri);
    }

    @Override
    public OAuthUserInfo fetchUserInfo(String code, String redirectUri) {
        String accessToken = kakaoOAuthClient.exchangeCodeForAccessToken(code, redirectUri);
        KakaoUserInfo info = kakaoOAuthClient.getUserInfo(accessToken);
        return new OAuthUserInfo(info.id(), info.email(), info.nickname());
    }
}
