package kr.co.jparangdev.boardbuddy.domain.auth.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.AuthException;
import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;

@SuppressWarnings("java:S110")
public class InvalidTokenException extends AuthException implements MessageResolvable {

    public InvalidTokenException(String message) {
        super(AuthErrorCode.INVALID_TOKEN, message);
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[0];
    }
}
