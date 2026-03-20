package kr.co.jparangdev.boardbuddy.domain.user.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;
import kr.co.jparangdev.boardbuddy.domain.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException implements MessageResolvable {

    private final Object identifier;

    public UserNotFoundException(Long userId) {
        super(UserErrorCode.USER_NOT_FOUND, "User not found: " + userId);
        this.identifier = userId;
    }

    public UserNotFoundException(String userTag) {
        super(UserErrorCode.USER_NOT_FOUND, "User not found: " + userTag);
        this.identifier = userTag;
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[]{identifier};
    }
}
