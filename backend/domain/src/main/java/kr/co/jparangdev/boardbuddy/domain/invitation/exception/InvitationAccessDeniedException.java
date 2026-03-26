package kr.co.jparangdev.boardbuddy.domain.invitation.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ForbiddenException;
import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;

@SuppressWarnings("java:S110")
public class InvitationAccessDeniedException extends ForbiddenException implements MessageResolvable {

    private final Long invitationId;
    private final Long userId;

    public InvitationAccessDeniedException(Long invitationId, Long userId) {
        super(InvitationErrorCode.INVITATION_ACCESS_DENIED,
              "User " + userId + " is not the invitee of invitation " + invitationId);
        this.invitationId = invitationId;
        this.userId = userId;
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[]{invitationId, userId};
    }
}
