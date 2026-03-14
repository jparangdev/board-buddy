package kr.co.jparangdev.boardbuddy.application.game.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.NotFoundException;
import kr.co.jparangdev.boardbuddy.domain.game.exception.GameErrorCode;

public class GameSessionNotFoundException extends NotFoundException {

    public GameSessionNotFoundException(Long sessionId) {
        super(GameErrorCode.GAME_SESSION_NOT_FOUND, "Game session not found: " + sessionId);
    }
}
