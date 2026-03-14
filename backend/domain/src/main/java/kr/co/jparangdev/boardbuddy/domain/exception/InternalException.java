package kr.co.jparangdev.boardbuddy.domain.exception;

public abstract class InternalException extends BusinessException {

    protected InternalException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected InternalException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
