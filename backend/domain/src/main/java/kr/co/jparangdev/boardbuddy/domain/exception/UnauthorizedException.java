package kr.co.jparangdev.boardbuddy.domain.exception;

public abstract class UnauthorizedException extends BusinessException {

    protected UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected UnauthorizedException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
