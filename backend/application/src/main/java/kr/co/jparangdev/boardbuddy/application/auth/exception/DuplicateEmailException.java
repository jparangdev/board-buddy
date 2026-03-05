package kr.co.jparangdev.boardbuddy.application.auth.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Email already in use: " + email);
    }
}
