package kr.co.jparangdev.boardbuddy.domain.exception;

public abstract class NotFoundException extends BusinessException {

    protected NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
