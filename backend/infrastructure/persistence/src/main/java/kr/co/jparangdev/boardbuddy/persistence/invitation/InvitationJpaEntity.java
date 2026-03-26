package kr.co.jparangdev.boardbuddy.persistence.invitation;

import java.time.Instant;

import jakarta.persistence.*;
import kr.co.jparangdev.boardbuddy.domain.invitation.InvitationStatus;
import lombok.*;

@Entity
@Table(name = "invitations",
    indexes = {
        @Index(name = "idx_invitations_invitee_status", columnList = "invitee_id, status")
    })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvitationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "inviter_id", nullable = false)
    private Long inviterId;

    @Column(name = "invitee_id", nullable = false)
    private Long inviteeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvitationStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public InvitationJpaEntity(Long id, Long groupId, Long inviterId, Long inviteeId,
                                InvitationStatus status, Instant createdAt, Instant respondedAt) {
        this.id = id;
        this.groupId = groupId;
        this.inviterId = inviterId;
        this.inviteeId = inviteeId;
        this.status = status;
        this.createdAt = createdAt;
        this.respondedAt = respondedAt;
    }
}
