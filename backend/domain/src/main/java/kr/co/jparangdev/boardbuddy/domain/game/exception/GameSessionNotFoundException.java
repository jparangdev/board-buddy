package kr.co.jparangdev.boardbuddy.domain.game.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;
import kr.co.jparangdev.boardbuddy.domain.exception.NotFoundException;

public class GameSessionNotFoundException extends NotFoundException implements MessageResolvable {

    private final Long sessionId;

    public GameSessionNotFoundException(Long sessionId) {
        super(GameErrorCode.GAME_SESSION_NOT_FOUND, "Game session not found: " + sessionId);
        this.sessionId = sessionId;
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[]{sessionId};
    }
}
