package kr.co.jparangdev.boardbuddy.domain.game.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCode;

public enum GameErrorCode implements ErrorCode {

    GAME_NOT_FOUND,
    GAME_SESSION_NOT_FOUND,
    DUPLICATE_GAME_NAME,
    CUSTOM_GAME_NOT_FOUND,
    DUPLICATE_CUSTOM_GAME_NAME;
}
