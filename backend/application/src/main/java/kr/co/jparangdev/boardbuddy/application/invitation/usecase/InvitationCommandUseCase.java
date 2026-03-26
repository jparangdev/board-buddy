package kr.co.jparangdev.boardbuddy.application.invitation.usecase;

public interface InvitationCommandUseCase {
    void inviteUser(Long groupId, Long inviteeId);
    void respondToInvitation(Long invitationId, boolean accept);
}
