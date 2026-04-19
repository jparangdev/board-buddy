package kr.co.jparangdev.boardbuddy.application.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthTokens;
import kr.co.jparangdev.boardbuddy.application.auth.dto.OAuthUserInfo;
import kr.co.jparangdev.boardbuddy.application.auth.usecase.OAuthLoginUseCase;
import kr.co.jparangdev.boardbuddy.domain.auth.ProviderType;
import kr.co.jparangdev.boardbuddy.domain.auth.repository.SocialAccountRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OAuthLoginServiceTest {

    private OAuthLoginUseCase service;

    @Mock
    private OAuthProvider kakaoProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SocialAccountRepository socialAccountRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private static final OAuthUserInfo KAKAO_USER = new OAuthUserInfo("kakao-111", "user@kakao.com", "KakaoUser");

    @BeforeEach
    void setUp() {
        given(kakaoProvider.getProviderType()).willReturn(ProviderType.KAKAO);
        service = new OAuthLoginService(
                List.of(kakaoProvider), userRepository, socialAccountRepository,
                jwtTokenProvider, refreshTokenRepository
        );
    }

    @Test
    @DisplayName("Login succeeds when social account is already linked")
    void loginWithOAuth_existingLinkedAccount() {
        given(kakaoProvider.fetchUserInfo("code", "http://redirect")).willReturn(KAKAO_USER);
        given(socialAccountRepository.findUserIdByProviderAndProviderId("KAKAO", "kakao-111"))
                .willReturn(Optional.of(42L));
        given(jwtTokenProvider.generateAccessToken(42L)).willReturn("access");
        given(jwtTokenProvider.generateRefreshToken(42L)).willReturn("refresh");
        given(jwtTokenProvider.getAccessTokenExpiry()).willReturn(3600L);

        AuthTokens tokens = service.loginWithOAuth("kakao", "code", "http://redirect");

        assertThat(tokens.getAccessToken()).isEqualTo("access");
        verify(refreshTokenRepository).save("refresh", 42L);
    }

    @Test
    @DisplayName("Login succeeds via users-table fallback and links account")
    void loginWithOAuth_fallbackToUsersTable() {
        User existingUser = User.builder().id(7L).provider("KAKAO").providerId("kakao-111").build();
        given(kakaoProvider.fetchUserInfo("code", "http://redirect")).willReturn(KAKAO_USER);
        given(socialAccountRepository.findUserIdByProviderAndProviderId("KAKAO", "kakao-111"))
                .willReturn(Optional.empty());
        given(userRepository.findByProviderAndProviderId("KAKAO", "kakao-111"))
                .willReturn(Optional.of(existingUser));
        given(jwtTokenProvider.generateAccessToken(7L)).willReturn("access");
        given(jwtTokenProvider.generateRefreshToken(7L)).willReturn("refresh");
        given(jwtTokenProvider.getAccessTokenExpiry()).willReturn(3600L);

        service.loginWithOAuth("kakao", "code", "http://redirect");

        verify(socialAccountRepository).save(7L, "KAKAO", "kakao-111");
    }

    @Test
    @DisplayName("New OAuth user is created and linked on first login")
    void loginWithOAuth_newUser() {
        User newUser = User.builder().id(99L).build();
        given(kakaoProvider.fetchUserInfo("code", "http://redirect")).willReturn(KAKAO_USER);
        given(socialAccountRepository.findUserIdByProviderAndProviderId(anyString(), anyString()))
                .willReturn(Optional.empty());
        given(userRepository.findByProviderAndProviderId(anyString(), anyString()))
                .willReturn(Optional.empty());
        given(userRepository.generateUniqueDiscriminator("KakaoUser")).willReturn("AB12");
        given(userRepository.save(any())).willReturn(newUser);
        given(jwtTokenProvider.generateAccessToken(99L)).willReturn("access");
        given(jwtTokenProvider.generateRefreshToken(99L)).willReturn("refresh");
        given(jwtTokenProvider.getAccessTokenExpiry()).willReturn(3600L);

        AuthTokens tokens = service.loginWithOAuth("kakao", "code", "http://redirect");

        assertThat(tokens.getAccessToken()).isEqualTo("access");
        verify(socialAccountRepository).save(99L, "KAKAO", "kakao-111");
    }

    @Test
    @DisplayName("getAuthorizationUrl delegates to provider")
    void getAuthorizationUrl_delegates() {
        given(kakaoProvider.buildAuthorizationUrl("http://redirect")).willReturn("https://kauth.kakao.com/...");

        String url = service.getAuthorizationUrl("kakao", "http://redirect");

        assertThat(url).startsWith("https://kauth.kakao.com");
    }
}
