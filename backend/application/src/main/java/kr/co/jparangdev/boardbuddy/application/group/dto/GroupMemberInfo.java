package kr.co.jparangdev.boardbuddy.application.group.dto;

import java.time.Instant;

public record GroupMemberInfo(
        Long userId,
        String nickname,
        String discriminator,
        String userTag,
        Instant joinedAt,
        GroupMemberStatus status
) {
}

