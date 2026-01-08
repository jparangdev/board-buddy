package kr.co.jparangdev.boardbuddy.application.auth;

import kr.co.jparangdev.boardbuddy.application.exception.InvalidTokenException;
import kr.co.jparangdev.boardbuddy.application.user.UserRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationUseCase {

    private final NaverOAuthClient naverOAuthClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    public String getNaverAuthorizationUrl() {
        return naverOAuthClient.getAuthorizationUrl();
    }

    @Override
    @Transactional
    public AuthTokens processNaverCallback(String code, String state) {
        // 1. Exchange code for Naver access token
        NaverTokenResponse naverToken = naverOAuthClient.getAccessToken(code, state);

        // 2. Get user info from Naver
        NaverUserInfo naverUserInfo = naverOAuthClient.getUserInfo(naverToken.getAccessToken());

        // 3. Find or create user
        User user = userRepository.findByProviderAndProviderId("NAVER", naverUserInfo.getId())
            .orElseGet(() -> registerNewUser(naverUserInfo));

        // 4. Generate JWT tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // 5. Store refresh token in Redis
        refreshTokenRepository.save(refreshToken, user.getId());

        return AuthTokens.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(jwtTokenProvider.getAccessTokenExpiry())
            .tokenType("Bearer")
            .build();
    }

    private User registerNewUser(NaverUserInfo naverUserInfo) {
        String email = naverUserInfo.getEmail();
        String nickname = extractNickname(naverUserInfo);
        String discriminator = userRepository.generateUniqueDiscriminator(nickname);

        User newUser = User.fromOAuth(
            email,
            "NAVER",
            naverUserInfo.getId(),
            nickname,
            discriminator
        );

        return userRepository.save(newUser);
    }

    private String extractNickname(NaverUserInfo info) {
        // Try nickname first, fallback to name, then email prefix
        if (info.getNickname() != null && !info.getNickname().isBlank()) {
            return info.getNickname();
        }
        if (info.getName() != null && !info.getName().isBlank()) {
            return info.getName();
        }
        return info.getEmail().split("@")[0];
    }

    @Override
    @Transactional
    public AuthTokens refreshAccessToken(String refreshToken) {
        Long userId = refreshTokenRepository.findUserIdByToken(refreshToken)
            .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);

        return AuthTokens.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken) // Same refresh token
            .expiresIn(jwtTokenProvider.getAccessTokenExpiry())
            .tokenType("Bearer")
            .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}
