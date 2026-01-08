package kr.co.jparangdev.boardbuddy.client.oauth;

import kr.co.jparangdev.boardbuddy.application.auth.NaverOAuthClient;
import kr.co.jparangdev.boardbuddy.application.auth.NaverTokenResponse;
import kr.co.jparangdev.boardbuddy.application.auth.NaverUserInfo;
import kr.co.jparangdev.boardbuddy.application.auth.NaverUserInfoResponse;
import kr.co.jparangdev.boardbuddy.application.exception.OAuthAuthenticationException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NaverOAuthClientImpl implements NaverOAuthClient {

	private final RestTemplate restTemplate;

	@Value("${oauth.naver.client-id}")
	private String clientId;

	@Value("${oauth.naver.client-secret}")
	private String clientSecret;

	@Value("${oauth.naver.redirect-uri}")
	private String redirectUri;

	@Override
	public String getAuthorizationUrl() {
		String state = UUID.randomUUID().toString();
		return UriComponentsBuilder
			.fromUriString("https://nid.naver.com/oauth2.0/authorize")
			.queryParam("response_type", "code")
			.queryParam("client_id", clientId)
			.queryParam("redirect_uri", redirectUri)
			.queryParam("state", state)
			.build()
			.toUriString();
	}

	@Override
	public NaverTokenResponse getAccessToken(String code, String state) {
		try {
			String url = UriComponentsBuilder
				.fromUriString("https://nid.naver.com/oauth2.0/token")
				.queryParam("grant_type", "authorization_code")
				.queryParam("client_id", clientId)
				.queryParam("client_secret", clientSecret)
				.queryParam("code", code)
				.queryParam("state", state)
				.build()
				.toUriString();

			ResponseEntity<NaverTokenResponse> response = restTemplate.exchange(
				url,
				HttpMethod.POST,
				null,
				NaverTokenResponse.class
			);

			return response.getBody();
		} catch (Exception e) {
			throw new OAuthAuthenticationException("Failed to get Naver access token", e);
		}
	}

	@Override
	public NaverUserInfo getUserInfo(String accessToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);
			HttpEntity<Void> entity = new HttpEntity<>(headers);

			ResponseEntity<NaverUserInfoResponse> response = restTemplate.exchange(
				"https://openapi.naver.com/v1/nid/me",
				HttpMethod.GET,
				entity,
				NaverUserInfoResponse.class
			);

			return response.getBody().getResponse();
		} catch (Exception e) {
			throw new OAuthAuthenticationException("Failed to get Naver user info", e);
		}
	}
}
