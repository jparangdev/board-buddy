package kr.co.jparangdev.boardbuddy.domain.exception;

public abstract class ConflictException extends BoardBuddyException {

    protected ConflictException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
