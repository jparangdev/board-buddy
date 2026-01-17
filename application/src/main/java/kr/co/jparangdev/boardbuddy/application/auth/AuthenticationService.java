package kr.co.jparangdev.boardbuddy.application.auth;

import kr.co.jparangdev.boardbuddy.application.exception.InvalidTokenException;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationUseCase {

    private final List<AuthenticationProvider> authenticationProviders;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public AuthTokens authenticate(AuthCredentials credentials) {
        // Find appropriate provider for the credentials
        AuthenticationProvider provider = findProvider(credentials.getProviderType());

        // Authenticate and get user
        User user = provider.authenticate(credentials);

        // Generate JWT tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // Store refresh token
        refreshTokenRepository.save(refreshToken, user.getId());

        return AuthTokens.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(jwtTokenProvider.getAccessTokenExpiry())
            .tokenType("Bearer")
            .build();
    }

    private AuthenticationProvider findProvider(ProviderType providerType) {
        return authenticationProviders.stream()
            .filter(provider -> provider.supports(providerType))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unsupported authentication provider: " + providerType));
    }

    @Override
    @Transactional
    public AuthTokens refreshAccessToken(String refreshToken) {
        Long userId = refreshTokenRepository.findUserIdByToken(refreshToken)
            .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);

        return AuthTokens.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken)
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
