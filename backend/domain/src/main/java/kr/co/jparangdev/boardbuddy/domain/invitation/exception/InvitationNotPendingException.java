package kr.co.jparangdev.boardbuddy.domain.invitation.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ConflictException;
import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;

@SuppressWarnings("java:S110")
public class InvitationNotPendingException extends ConflictException implements MessageResolvable {

    private final Long invitationId;

    public InvitationNotPendingException(Long invitationId) {
        super(InvitationErrorCode.INVITATION_NOT_PENDING,
              "Invitation is not in PENDING state: " + invitationId);
        this.invitationId = invitationId;
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[]{invitationId};
    }
}
