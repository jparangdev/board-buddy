package kr.co.jparangdev.boardbuddy.application.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthCredentials;
import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthTokens;
import kr.co.jparangdev.boardbuddy.domain.auth.ProviderType;
import kr.co.jparangdev.boardbuddy.domain.auth.exception.InvalidTokenException;
import kr.co.jparangdev.boardbuddy.domain.user.User;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
            List.of(authenticationProvider),
            jwtTokenProvider,
            refreshTokenRepository
        );
    }

    @Test
    @DisplayName("Normally Authenticate")
    void authenticateSuccess() {
        // given
        AuthCredentials credentials = mock(AuthCredentials.class);
        given(credentials.getProviderType()).willReturn(ProviderType.LOCAL);

        User user = User.builder().id(1L).email("test@example.com").build();

        given(authenticationProvider.supports(ProviderType.LOCAL)).willReturn(true);
        given(authenticationProvider.authenticate(credentials)).willReturn(user);
        given(jwtTokenProvider.generateAccessToken(1L)).willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken(1L)).willReturn("refresh-token");
        given(jwtTokenProvider.getAccessTokenExpiry()).willReturn(3600000L);

        // when
        AuthTokens tokens = authenticationService.authenticate(credentials);

        // then
        assertThat(tokens.getAccessToken()).isEqualTo("access-token");
        assertThat(tokens.getRefreshToken()).isEqualTo("refresh-token");
        verify(refreshTokenRepository).save("refresh-token", 1L);
    }

    @Test
    @DisplayName("Throw exception when provider is not supported")
    void authenticateUnsupportedProvider() {
        // given
        AuthCredentials credentials = mock(AuthCredentials.class);
        given(credentials.getProviderType()).willReturn(ProviderType.KAKAO);

        given(authenticationProvider.supports(ProviderType.KAKAO)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authenticationService.authenticate(credentials))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unsupported authentication provider");
    }

    @Test
    @DisplayName("Refresh Access Token Success")
    void refreshAccessTokenSuccess() {
        // given
        String refreshToken = "valid-refresh-token";
        Long userId = 1L;

        given(refreshTokenRepository.findUserIdByToken(refreshToken)).willReturn(Optional.of(userId));
        given(jwtTokenProvider.generateAccessToken(userId)).willReturn("new-access-token");
        given(jwtTokenProvider.getAccessTokenExpiry()).willReturn(3600000L);

        // when
        AuthTokens tokens = authenticationService.refreshAccessToken(refreshToken);

        // then
        assertThat(tokens.getAccessToken()).isEqualTo("new-access-token");
        assertThat(tokens.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("Refresh Token Failed - Invalid Token")
    void refreshAccessTokenInvalid() {
        // given
        String refreshToken = "invalid-refresh-token";
        given(refreshTokenRepository.findUserIdByToken(refreshToken)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authenticationService.refreshAccessToken(refreshToken))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("Logout Success")
    void logoutSuccess() {
        // given
        String refreshToken = "refresh-token";

        // when
        authenticationService.logout(refreshToken);

        // then
        verify(refreshTokenRepository).delete(refreshToken);
    }
}
