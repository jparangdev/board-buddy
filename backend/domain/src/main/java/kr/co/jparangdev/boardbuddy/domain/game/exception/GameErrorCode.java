package kr.co.jparangdev.boardbuddy.domain.game.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCategory;
import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCode;

public enum GameErrorCode implements ErrorCode {

    GAME_NOT_FOUND(ErrorCategory.NOT_FOUND),
    GAME_SESSION_NOT_FOUND(ErrorCategory.NOT_FOUND),
    DUPLICATE_GAME_NAME(ErrorCategory.CONFLICT),
    CUSTOM_GAME_NOT_FOUND(ErrorCategory.NOT_FOUND),
    DUPLICATE_CUSTOM_GAME_NAME(ErrorCategory.CONFLICT);

    private final ErrorCategory category;

    GameErrorCode(ErrorCategory category) {
        this.category = category;
    }

    @Override
    public ErrorCategory getCategory() {
        return category;
    }
}
