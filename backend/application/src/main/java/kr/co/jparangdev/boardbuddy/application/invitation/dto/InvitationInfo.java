package kr.co.jparangdev.boardbuddy.application.invitation.dto;

import java.time.Instant;

public record InvitationInfo(
    Long id,
    Long groupId,
    String groupName,
    Long inviterId,
    String inviterNickname,
    Long inviteeId,
    String inviteeNickname,
    String status,
    Instant createdAt
) {}
