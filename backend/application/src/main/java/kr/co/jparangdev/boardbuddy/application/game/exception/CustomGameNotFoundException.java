package kr.co.jparangdev.boardbuddy.application.game.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.NotFoundException;
import kr.co.jparangdev.boardbuddy.domain.game.exception.GameErrorCode;

public class CustomGameNotFoundException extends NotFoundException {

    public CustomGameNotFoundException(Long customGameId) {
        super(GameErrorCode.CUSTOM_GAME_NOT_FOUND, "Custom game not found: " + customGameId);
    }
}
