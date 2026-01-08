package kr.co.jparangdev.boardbuddy.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByEmail(String email);

    Optional<UserJpaEntity> findByProviderAndProviderId(String provider, String providerId);

    Optional<UserJpaEntity> findByNicknameAndDiscriminator(String nickname, String discriminator);

    boolean existsByEmail(String email);
}
