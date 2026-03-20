package kr.co.jparangdev.boardbuddy.domain.auth.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.AuthException;
import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;

public class OAuthAuthenticationException extends AuthException implements MessageResolvable {

    public OAuthAuthenticationException(String message, Throwable cause) {
        super(AuthErrorCode.OAUTH_AUTHENTICATION_FAILED, message, cause);
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
