package kr.co.jparangdev.boardbuddy.application.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthTokens;
import kr.co.jparangdev.boardbuddy.application.auth.dto.OAuthUserInfo;
import kr.co.jparangdev.boardbuddy.application.auth.usecase.OAuthLoginUseCase;
import kr.co.jparangdev.boardbuddy.domain.auth.ProviderType;
import kr.co.jparangdev.boardbuddy.domain.auth.exception.OAuthAuthenticationException;
import kr.co.jparangdev.boardbuddy.domain.auth.repository.SocialAccountRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthLoginService implements OAuthLoginUseCase {

    private final List<OAuthProvider> oAuthProviders;
    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public String getAuthorizationUrl(String provider, String redirectUri) {
        return findProvider(provider).buildAuthorizationUrl(redirectUri);
    }

    @Override
    @Transactional
    public AuthTokens loginWithOAuth(String provider, String code, String redirectUri) {
        OAuthProvider oAuthProvider = findProvider(provider);
        OAuthUserInfo userInfo = oAuthProvider.fetchUserInfo(code, redirectUri);
        String providerName = oAuthProvider.getProviderType().name();

        Long userId = resolveUserId(providerName, userInfo);

        String accessToken = jwtTokenProvider.generateAccessToken(userId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);
        refreshTokenRepository.save(refreshToken, userId);

        return AuthTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getAccessTokenExpiry())
                .tokenType("Bearer")
                .build();
    }

    private Long resolveUserId(String providerName, OAuthUserInfo userInfo) {
        // 1. Check user_social_accounts (linked accounts)
        var linkedUserId = socialAccountRepository.findUserIdByProviderAndProviderId(
                providerName, userInfo.externalId());
        if (linkedUserId.isPresent()) {
            return linkedUserId.get();
        }

        // 2. Fallback: check users table primary provider (backward compat)
        var existingUser = userRepository.findByProviderAndProviderId(providerName, userInfo.externalId());
        if (existingUser.isPresent()) {
            Long userId = existingUser.get().getId();
            socialAccountRepository.save(userId, providerName, userInfo.externalId());
            return userId;
        }

        // 3. New OAuth user — create account and link
        String discriminator = userRepository.generateUniqueDiscriminator(userInfo.nickname());
        String email = userInfo.email() != null ? userInfo.email()
                : providerName.toLowerCase() + "_" + userInfo.externalId() + "@noemail.invalid";
        User newUser = User.fromOAuth(email, providerName, userInfo.externalId(),
                userInfo.nickname(), discriminator);
        User saved = userRepository.save(newUser);
        socialAccountRepository.save(saved.getId(), providerName, userInfo.externalId());
        return saved.getId();
    }

    private OAuthProvider findProvider(String provider) {
        ProviderType type;
        try {
            type = ProviderType.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OAuthAuthenticationException("Unsupported OAuth provider: " + provider, e);
        }

        return oAuthProviders.stream()
                .filter(p -> p.getProviderType() == type)
                .findFirst()
                .orElseThrow(() -> new OAuthAuthenticationException(
                        "No implementation found for provider: " + provider, null));
    }
}
