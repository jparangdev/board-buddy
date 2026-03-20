package kr.co.jparangdev.boardbuddy.domain.exception;

public abstract class AuthException extends BoardBuddyException {

    protected AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected AuthException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
