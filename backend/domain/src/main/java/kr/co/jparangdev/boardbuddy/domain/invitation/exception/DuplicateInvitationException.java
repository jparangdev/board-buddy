package kr.co.jparangdev.boardbuddy.domain.invitation.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ConflictException;
import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;

@SuppressWarnings("java:S110")
public class DuplicateInvitationException extends ConflictException implements MessageResolvable {

    private final Long groupId;
    private final Long inviteeId;

    public DuplicateInvitationException(Long groupId, Long inviteeId) {
        super(InvitationErrorCode.DUPLICATE_INVITATION,
              "Invitation already exists or user is already a member: invitee=" + inviteeId + " group=" + groupId);
        this.groupId = groupId;
        this.inviteeId = inviteeId;
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[]{groupId, inviteeId};
    }
}
