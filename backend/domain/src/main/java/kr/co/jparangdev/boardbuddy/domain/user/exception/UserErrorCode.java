package kr.co.jparangdev.boardbuddy.domain.user.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCategory;
import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(ErrorCategory.NOT_FOUND),
    USER_NOT_GROUP_MEMBER(ErrorCategory.FORBIDDEN);

    private final ErrorCategory category;

    UserErrorCode(ErrorCategory category) {
        this.category = category;
    }

    @Override
    public ErrorCategory getCategory() {
        return category;
    }
}
