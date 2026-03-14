package kr.co.jparangdev.boardbuddy.application.auth.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.UnauthorizedException;

public class OAuthAuthenticationException extends UnauthorizedException {

    public OAuthAuthenticationException(String message, Throwable cause) {
        super(AuthErrorCode.OAUTH_AUTHENTICATION_FAILED, message, cause);
    }
}
