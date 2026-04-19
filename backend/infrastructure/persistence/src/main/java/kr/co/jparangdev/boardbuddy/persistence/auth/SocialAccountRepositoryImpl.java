package kr.co.jparangdev.boardbuddy.persistence.auth;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.domain.auth.SocialAccount;
import kr.co.jparangdev.boardbuddy.domain.auth.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SocialAccountRepositoryImpl implements SocialAccountRepository {

    private final SocialAccountJpaRepository jpaRepository;

    @Override
    @Transactional
    public void save(Long userId, String provider, String providerId) {
        jpaRepository.save(new SocialAccountJpaEntity(userId, provider, providerId, Instant.now()));
    }

    @Override
    public Optional<Long> findUserIdByProviderAndProviderId(String provider, String providerId) {
        return jpaRepository.findByProviderAndProviderId(provider, providerId)
                .map(SocialAccountJpaEntity::getUserId);
    }

    @Override
    public List<SocialAccount> findAllByUserId(Long userId) {
        return jpaRepository.findAllByUserId(userId).stream()
                .map(e -> new SocialAccount(e.getUserId(), e.getProvider(), e.getProviderId(), e.getCreatedAt()))
                .toList();
    }

    @Override
    @Transactional
    public void deleteByUserIdAndProvider(Long userId, String provider) {
        jpaRepository.deleteByUserIdAndProvider(userId, provider);
    }

    @Override
    public boolean existsByUserIdAndProvider(Long userId, String provider) {
        return jpaRepository.existsByUserIdAndProvider(userId, provider);
    }
}
