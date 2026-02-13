package kr.co.jparangdev.boardbuddy.persistence.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByEmail(String email);

    Optional<UserJpaEntity> findByProviderAndProviderId(String provider, String providerId);

    Optional<UserJpaEntity> findByNicknameAndDiscriminator(String nickname, String discriminator);

    boolean existsByEmail(String email);

    List<UserJpaEntity> findByNicknameContainingIgnoreCase(String keyword);
}
