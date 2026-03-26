package kr.co.jparangdev.boardbuddy.domain.invitation.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCode;

public enum InvitationErrorCode implements ErrorCode {
    INVITATION_NOT_FOUND,
    DUPLICATE_INVITATION,
    INVITATION_NOT_PENDING,
    INVITATION_ACCESS_DENIED
}
