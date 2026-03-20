package kr.co.jparangdev.boardbuddy.persistence.auth;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredRefreshTokenCleaner {

    private final RefreshTokenJpaRepository jpaRepository;

    /** Runs daily at 03:00 to purge expired refresh tokens. */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeExpiredTokens() {
        jpaRepository.deleteAllExpiredBefore(Instant.now());
        log.debug("Purged expired refresh tokens");
    }
}
