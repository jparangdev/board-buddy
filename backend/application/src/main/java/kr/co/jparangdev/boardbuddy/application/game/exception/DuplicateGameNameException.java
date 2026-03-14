package kr.co.jparangdev.boardbuddy.application.game.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ConflictException;
import kr.co.jparangdev.boardbuddy.domain.game.exception.GameErrorCode;

public class DuplicateGameNameException extends ConflictException {

    public DuplicateGameNameException(String name) {
        super(GameErrorCode.DUPLICATE_GAME_NAME, "Game with name already exists: " + name);
    }
}
