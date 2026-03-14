package kr.co.jparangdev.boardbuddy.application.auth.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ConflictException;

public class DuplicateEmailException extends ConflictException {

    public DuplicateEmailException(String email) {
        super(AuthErrorCode.DUPLICATE_EMAIL, "Email already in use: " + email);
    }
}
