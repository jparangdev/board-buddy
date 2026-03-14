package kr.co.jparangdev.boardbuddy.application.auth.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCategory;
import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

    INVALID_CREDENTIALS(ErrorCategory.UNAUTHORIZED),
    INVALID_TOKEN(ErrorCategory.UNAUTHORIZED),
    OAUTH_AUTHENTICATION_FAILED(ErrorCategory.UNAUTHORIZED),
    DUPLICATE_EMAIL(ErrorCategory.CONFLICT);

    private final ErrorCategory category;

    AuthErrorCode(ErrorCategory category) {
        this.category = category;
    }

    @Override
    public ErrorCategory getCategory() {
        return category;
    }
}
