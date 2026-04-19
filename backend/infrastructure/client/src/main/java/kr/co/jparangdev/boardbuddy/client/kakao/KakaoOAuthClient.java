package kr.co.jparangdev.boardbuddy.client.kakao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import kr.co.jparangdev.boardbuddy.domain.auth.exception.OAuthAuthenticationException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    public String exchangeCodeForAccessToken(String code, String redirectUri) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    TOKEN_URL,
                    HttpMethod.POST,
                    new HttpEntity<>(params, headers),
                    Map.class
            );
            Object accessToken = response.getBody() != null ? response.getBody().get("access_token") : null;
            if (accessToken == null) {
                throw new OAuthAuthenticationException("Kakao token exchange returned no access_token", null);
            }
            return accessToken.toString();
        } catch (OAuthAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new OAuthAuthenticationException("Failed to exchange Kakao authorization code", e);
        }
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    USER_INFO_URL,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );
            Map<?, ?> body = response.getBody();
            if (body == null) {
                throw new OAuthAuthenticationException("Kakao user info response is empty", null);
            }

            String id = body.get("id").toString();
            Map<?, ?> kakaoAccount = (Map<?, ?>) body.get("kakao_account");
            String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

            Map<?, ?> profile = kakaoAccount != null ? (Map<?, ?>) kakaoAccount.get("profile") : null;
            String nickname = profile != null ? (String) profile.get("nickname") : null;
            if (nickname == null || nickname.isBlank()) {
                nickname = "User" + id.substring(Math.max(0, id.length() - 4));
            }

            return new KakaoUserInfo(id, email, nickname);
        } catch (OAuthAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new OAuthAuthenticationException("Failed to fetch Kakao user info", e);
        }
    }

    public String buildAuthorizationUrl(String redirectUri) {
        return "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=profile_nickname,account_email";
    }
}
