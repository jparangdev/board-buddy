package kr.co.jparangdev.boardbuddy.application.auth.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.UnauthorizedException;

public class InvalidCredentialsException extends UnauthorizedException {

    public InvalidCredentialsException() {
        super(AuthErrorCode.INVALID_CREDENTIALS, "Invalid email or password");
    }
}
