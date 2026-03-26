package kr.co.jparangdev.boardbuddy.domain.invitation;

import java.time.Instant;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitation {
    private Long id;
    private Long groupId;
    private Long inviterId;
    private Long inviteeId;
    private InvitationStatus status;
    private Instant createdAt;
    private Instant respondedAt;

    @Builder
    public Invitation(Long id, Long groupId, Long inviterId, Long inviteeId,
                      InvitationStatus status, Instant createdAt, Instant respondedAt) {
        this.id = id;
        this.groupId = groupId;
        this.inviterId = inviterId;
        this.inviteeId = inviteeId;
        this.status = status;
        this.createdAt = createdAt;
        this.respondedAt = respondedAt;
    }

    public static Invitation create(Long groupId, Long inviterId, Long inviteeId) {
        return Invitation.builder()
                .groupId(groupId)
                .inviterId(inviterId)
                .inviteeId(inviteeId)
                .status(InvitationStatus.PENDING)
                .createdAt(Instant.now())
                .build();
    }

    public boolean isPending() {
        return this.status == InvitationStatus.PENDING;
    }

    public Invitation accept() {
        return Invitation.builder()
                .id(this.id)
                .groupId(this.groupId)
                .inviterId(this.inviterId)
                .inviteeId(this.inviteeId)
                .status(InvitationStatus.ACCEPTED)
                .createdAt(this.createdAt)
                .respondedAt(Instant.now())
                .build();
    }

    public Invitation reject() {
        return Invitation.builder()
                .id(this.id)
                .groupId(this.groupId)
                .inviterId(this.inviterId)
                .inviteeId(this.inviteeId)
                .status(InvitationStatus.REJECTED)
                .createdAt(this.createdAt)
                .respondedAt(Instant.now())
                .build();
    }
}
