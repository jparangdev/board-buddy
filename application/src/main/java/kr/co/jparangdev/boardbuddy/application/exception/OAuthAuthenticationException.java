package kr.co.jparangdev.boardbuddy.application.exception;

public class OAuthAuthenticationException extends RuntimeException {
    public OAuthAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
