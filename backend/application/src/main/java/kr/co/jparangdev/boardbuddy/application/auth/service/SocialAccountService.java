package kr.co.jparangdev.boardbuddy.application.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.auth.dto.OAuthUserInfo;
import kr.co.jparangdev.boardbuddy.application.auth.dto.SocialAccountDto;
import kr.co.jparangdev.boardbuddy.application.auth.usecase.SocialAccountUseCase;
import kr.co.jparangdev.boardbuddy.domain.auth.ProviderType;
import kr.co.jparangdev.boardbuddy.domain.auth.SocialAccount;
import kr.co.jparangdev.boardbuddy.domain.auth.exception.CannotUnlinkLastMethodException;
import kr.co.jparangdev.boardbuddy.domain.auth.exception.OAuthAuthenticationException;
import kr.co.jparangdev.boardbuddy.domain.auth.exception.ProviderAlreadyLinkedException;
import kr.co.jparangdev.boardbuddy.domain.auth.repository.SocialAccountRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialAccountService implements SocialAccountUseCase {

    private final List<OAuthProvider> oAuthProviders;
    private final SocialAccountRepository socialAccountRepository;
    private final UserRepository userRepository;

    @Override
    public List<SocialAccountDto> getLinkedAccounts(Long userId) {
        return socialAccountRepository.findAllByUserId(userId).stream()
                .map(sa -> new SocialAccountDto(sa.provider(), sa.createdAt()))
                .toList();
    }

    @Override
    @Transactional
    public void linkAccount(Long userId, String provider, String code, String redirectUri) {
        OAuthProvider oAuthProvider = findProvider(provider);
        String providerName = oAuthProvider.getProviderType().name();

        if (socialAccountRepository.existsByUserIdAndProvider(userId, providerName)) {
            throw new ProviderAlreadyLinkedException(providerName);
        }

        OAuthUserInfo userInfo = oAuthProvider.fetchUserInfo(code, redirectUri);

        // Check if this OAuth account is already linked to a different user
        socialAccountRepository.findUserIdByProviderAndProviderId(providerName, userInfo.externalId())
                .ifPresent(existingUserId -> {
                    if (!existingUserId.equals(userId)) {
                        throw new ProviderAlreadyLinkedException(providerName);
                    }
                });

        // Also check the primary provider in users table
        userRepository.findByProviderAndProviderId(providerName, userInfo.externalId())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(userId)) {
                        throw new ProviderAlreadyLinkedException(providerName);
                    }
                });

        socialAccountRepository.save(userId, providerName, userInfo.externalId());
    }

    @Override
    @Transactional
    public void unlinkAccount(Long userId, String provider) {
        String providerName = provider.toUpperCase();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        List<SocialAccount> linkedAccounts = socialAccountRepository.findAllByUserId(userId);

        boolean hasPassword = user.getPasswordHash() != null;
        boolean isPrimaryProvider = providerName.equals(user.getProvider());
        long remainingSocialAccounts = linkedAccounts.stream()
                .filter(sa -> !sa.provider().equals(providerName))
                .count();

        // Cannot unlink if this is the only login method
        if (!hasPassword && remainingSocialAccounts == 0) {
            // Check if primary provider entry in users table counts as another method
            if (!isPrimaryProvider || remainingSocialAccounts == 0) {
                throw new CannotUnlinkLastMethodException();
            }
        }

        socialAccountRepository.deleteByUserIdAndProvider(userId, providerName);
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
