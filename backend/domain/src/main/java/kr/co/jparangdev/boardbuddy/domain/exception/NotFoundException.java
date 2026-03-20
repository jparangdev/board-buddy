package kr.co.jparangdev.boardbuddy.domain.exception;

public abstract class NotFoundException extends BoardBuddyException {

    protected NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
