package kr.co.jparangdev.boardbuddy.domain.game.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;
import kr.co.jparangdev.boardbuddy.domain.exception.NotFoundException;

@SuppressWarnings("java:S110")
public class GameNotFoundException extends NotFoundException implements MessageResolvable {

    private final Long gameId;

    public GameNotFoundException(Long gameId) {
        super(GameErrorCode.GAME_NOT_FOUND, "Game not found: " + gameId);
        this.gameId = gameId;
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[]{gameId};
    }
}
