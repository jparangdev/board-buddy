package kr.co.jparangdev.boardbuddy.domain.invitation.repository;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddy.domain.invitation.Invitation;
import kr.co.jparangdev.boardbuddy.domain.invitation.InvitationStatus;

public interface InvitationRepository {
    Invitation save(Invitation invitation);
    Optional<Invitation> findById(Long id);
    List<Invitation> findAllByGroupIdAndStatus(Long groupId, InvitationStatus status);
    List<Invitation> findAllByInviteeIdAndStatus(Long inviteeId, InvitationStatus status);
    List<Invitation> findAllByInviterId(Long inviterId);
    boolean existsByGroupIdAndInviteeIdAndStatus(Long groupId, Long inviteeId, InvitationStatus status);
    void deleteAllByGroupId(Long groupId);
}
