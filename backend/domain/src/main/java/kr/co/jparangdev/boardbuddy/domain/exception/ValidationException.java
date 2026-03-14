package kr.co.jparangdev.boardbuddy.domain.exception;

public abstract class ValidationException extends BusinessException {

    protected ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
