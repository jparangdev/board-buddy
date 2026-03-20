package kr.co.jparangdev.boardbuddy.domain.exception;

public abstract class ForbiddenException extends BoardBuddyException {

    protected ForbiddenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
