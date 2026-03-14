package kr.co.jparangdev.boardbuddy.application.game.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ConflictException;
import kr.co.jparangdev.boardbuddy.domain.game.exception.GameErrorCode;

public class DuplicateCustomGameNameException extends ConflictException {

    public DuplicateCustomGameNameException(Long groupId, String name) {
        super(GameErrorCode.DUPLICATE_CUSTOM_GAME_NAME, "Custom game with name already exists in group " + groupId + ": " + name);
    }
}
