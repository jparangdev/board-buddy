package kr.co.jparangdev.boardbuddy.api.invitation;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.api.invitation.dto.InvitationDto;
import kr.co.jparangdev.boardbuddy.application.invitation.dto.InvitationInfo;

@Component
public class InvitationDtoMapper {

    public InvitationDto.Response toResponse(InvitationInfo info) {
        return InvitationDto.Response.builder()
            .id(info.id())
            .groupId(info.groupId())
            .groupName(info.groupName())
            .inviterId(info.inviterId())
            .inviterNickname(info.inviterNickname())
            .inviteeId(info.inviteeId())
            .createdAt(info.createdAt())
            .build();
    }
}
