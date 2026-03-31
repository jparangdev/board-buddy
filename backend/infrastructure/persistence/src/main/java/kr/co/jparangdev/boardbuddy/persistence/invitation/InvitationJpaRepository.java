package kr.co.jparangdev.boardbuddy.persistence.invitation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.jparangdev.boardbuddy.domain.invitation.InvitationStatus;

public interface InvitationJpaRepository extends JpaRepository<InvitationJpaEntity, Long> {
    List<InvitationJpaEntity> findAllByGroupIdAndStatus(Long groupId, InvitationStatus status);
    List<InvitationJpaEntity> findAllByInviteeIdAndStatus(Long inviteeId, InvitationStatus status);
    List<InvitationJpaEntity> findAllByInviterId(Long inviterId);
    boolean existsByGroupIdAndInviteeIdAndStatus(Long groupId, Long inviteeId, InvitationStatus status);
    void deleteAllByGroupId(Long groupId);
}
