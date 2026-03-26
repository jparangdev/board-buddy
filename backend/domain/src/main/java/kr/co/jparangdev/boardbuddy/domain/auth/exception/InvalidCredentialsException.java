package kr.co.jparangdev.boardbuddy.domain.auth.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.AuthException;
import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;

@SuppressWarnings("java:S110")
public class InvalidCredentialsException extends AuthException implements MessageResolvable {

    public InvalidCredentialsException() {
        super(AuthErrorCode.INVALID_CREDENTIALS, "Invalid email or password");
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
