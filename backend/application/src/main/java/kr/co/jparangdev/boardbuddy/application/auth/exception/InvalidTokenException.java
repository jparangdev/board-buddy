package kr.co.jparangdev.boardbuddy.application.auth.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.UnauthorizedException;

public class InvalidTokenException extends UnauthorizedException {

    public InvalidTokenException(String message) {
        super(AuthErrorCode.INVALID_TOKEN, message);
    }
}
