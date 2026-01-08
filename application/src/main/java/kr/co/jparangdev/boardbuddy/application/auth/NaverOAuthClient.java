package kr.co.jparangdev.boardbuddy.application.auth;

public interface NaverOAuthClient {
    String getAuthorizationUrl();
    NaverTokenResponse getAccessToken(String code, String state);
    NaverUserInfo getUserInfo(String accessToken);
}
