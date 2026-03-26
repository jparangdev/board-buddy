package kr.co.jparangdev.boardbuddy.persistence.invitation;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.domain.invitation.Invitation;

@Component
public class InvitationMapper {

    public InvitationJpaEntity toEntity(Invitation invitation) {
        return new InvitationJpaEntity(
            invitation.getId(),
            invitation.getGroupId(),
            invitation.getInviterId(),
            invitation.getInviteeId(),
            invitation.getStatus(),
            invitation.getCreatedAt(),
            invitation.getRespondedAt()
        );
    }

    public Invitation toDomain(InvitationJpaEntity entity) {
        return Invitation.builder()
            .id(entity.getId())
            .groupId(entity.getGroupId())
            .inviterId(entity.getInviterId())
            .inviteeId(entity.getInviteeId())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .respondedAt(entity.getRespondedAt())
            .build();
    }
}
