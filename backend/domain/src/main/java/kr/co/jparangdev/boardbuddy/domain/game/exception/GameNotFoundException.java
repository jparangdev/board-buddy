package kr.co.jparangdev.boardbuddy.domain.game.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.NotFoundException;

public class GameNotFoundException extends NotFoundException {

    public GameNotFoundException(Long gameId) {
        super(GameErrorCode.GAME_NOT_FOUND, "Game not found: " + gameId);
    }
}
