package kr.co.jparangdev.boardbuddy.domain.invitation.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;
import kr.co.jparangdev.boardbuddy.domain.exception.NotFoundException;

public class InvitationNotFoundException extends NotFoundException implements MessageResolvable {

    private final Long invitationId;

    public InvitationNotFoundException(Long invitationId) {
        super(InvitationErrorCode.INVITATION_NOT_FOUND, "Invitation not found: " + invitationId);
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
