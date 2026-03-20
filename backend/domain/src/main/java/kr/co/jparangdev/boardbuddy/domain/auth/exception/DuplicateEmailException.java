package kr.co.jparangdev.boardbuddy.domain.auth.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ConflictException;
import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;

public class DuplicateEmailException extends ConflictException implements MessageResolvable {

    private final String email;

    public DuplicateEmailException(String email) {
        super(AuthErrorCode.DUPLICATE_EMAIL, "Email already in use: " + email);
        this.email = email;
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[]{email};
    }
}
