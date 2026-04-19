package kr.co.jparangdev.boardbuddy.application.auth.dto;

import java.time.Instant;

public record SocialAccountDto(
        String provider,
        Instant linkedAt
) {
}
