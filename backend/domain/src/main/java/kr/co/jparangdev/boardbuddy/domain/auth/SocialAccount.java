package kr.co.jparangdev.boardbuddy.domain.auth;

import java.time.Instant;

public record SocialAccount(
        Long userId,
        String provider,
        String providerId,
        Instant createdAt
) {
}
