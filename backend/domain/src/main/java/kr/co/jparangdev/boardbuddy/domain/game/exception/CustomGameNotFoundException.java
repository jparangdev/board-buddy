package kr.co.jparangdev.boardbuddy.domain.game.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;
import kr.co.jparangdev.boardbuddy.domain.exception.NotFoundException;

public class CustomGameNotFoundException extends NotFoundException implements MessageResolvable {

    private final Long customGameId;

    public CustomGameNotFoundException(Long customGameId) {
        super(GameErrorCode.CUSTOM_GAME_NOT_FOUND, "Custom game not found: " + customGameId);
        this.customGameId = customGameId;
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[]{customGameId};
    }
}
