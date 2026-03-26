package kr.co.jparangdev.boardbuddy.domain.exception;

import lombok.Getter;

@Getter
public abstract class BoardBuddyException extends RuntimeException {

    private final ErrorCode errorCode;

    protected BoardBuddyException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected BoardBuddyException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
