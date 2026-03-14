package kr.co.jparangdev.boardbuddy.application.game.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.NotFoundException;
import kr.co.jparangdev.boardbuddy.domain.game.exception.GameErrorCode;

public class GameNotFoundException extends NotFoundException {

    public GameNotFoundException(Long gameId) {
        super(GameErrorCode.GAME_NOT_FOUND, "Game not found: " + gameId);
    }
}
