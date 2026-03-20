package kr.co.jparangdev.boardbuddy.domain.exception;

public abstract class InfrastructureException extends BoardBuddyException {

    protected InfrastructureException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected InfrastructureException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
