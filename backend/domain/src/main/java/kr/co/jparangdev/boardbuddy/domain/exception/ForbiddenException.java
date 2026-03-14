package kr.co.jparangdev.boardbuddy.domain.exception;

public abstract class ForbiddenException extends BusinessException {

    protected ForbiddenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
