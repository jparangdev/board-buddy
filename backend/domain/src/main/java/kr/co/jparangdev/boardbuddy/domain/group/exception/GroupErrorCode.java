package kr.co.jparangdev.boardbuddy.domain.group.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCategory;
import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCode;

public enum GroupErrorCode implements ErrorCode {

    GROUP_NOT_FOUND(ErrorCategory.NOT_FOUND),
    NOT_GROUP_OWNER(ErrorCategory.FORBIDDEN);

    private final ErrorCategory category;

    GroupErrorCode(ErrorCategory category) {
        this.category = category;
    }

    @Override
    public ErrorCategory getCategory() {
        return category;
    }
}
