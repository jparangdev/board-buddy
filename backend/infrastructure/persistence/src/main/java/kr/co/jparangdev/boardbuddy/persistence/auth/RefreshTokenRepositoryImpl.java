package kr.co.jparangdev.boardbuddy.persistence.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.auth.service.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private static final int TTL_DAYS = 30;

    private final RefreshTokenJpaRepository jpaRepository;

    @Override
    @Transactional
    public void save(String token, Long userId) {
        Instant expiresAt = Instant.now().plus(TTL_DAYS, ChronoUnit.DAYS);
        jpaRepository.save(new RefreshTokenJpaEntity(token, userId, expiresAt));
    }

    @Override
    public Optional<Long> findUserIdByToken(String token) {
        return jpaRepository.findByTokenAndExpiresAtAfter(token, Instant.now())
                .map(RefreshTokenJpaEntity::getUserId);
    }

    @Override
    @Transactional
    public void delete(String token) {
        jpaRepository.deleteByToken(token);
    }
}
