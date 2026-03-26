package kr.co.jparangdev.boardbuddy.domain.game.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ConflictException;
import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;

@SuppressWarnings("java:S110")
public class DuplicateGameNameException extends ConflictException implements MessageResolvable {

    private final String name;

    public DuplicateGameNameException(String name) {
        super(GameErrorCode.DUPLICATE_GAME_NAME, "Game with name already exists: " + name);
        this.name = name;
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[]{name};
    }
}
