package kr.co.jparangdev.boardbuddy.domain.auth.repository;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddy.domain.auth.SocialAccount;

public interface SocialAccountRepository {

    void save(Long userId, String provider, String providerId);

    Optional<Long> findUserIdByProviderAndProviderId(String provider, String providerId);

    List<SocialAccount> findAllByUserId(Long userId);

    void deleteByUserIdAndProvider(Long userId, String provider);

    boolean existsByUserIdAndProvider(Long userId, String provider);
}
