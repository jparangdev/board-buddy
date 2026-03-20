package kr.co.jparangdev.boardbuddy.domain.game.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ConflictException;
import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;

public class DuplicateCustomGameNameException extends ConflictException implements MessageResolvable {

    private final Long groupId;
    private final String name;

    public DuplicateCustomGameNameException(Long groupId, String name) {
        super(GameErrorCode.DUPLICATE_CUSTOM_GAME_NAME, "Custom game with name already exists in group " + groupId + ": " + name);
        this.groupId = groupId;
        this.name = name;
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[]{groupId, name};
    }
}
