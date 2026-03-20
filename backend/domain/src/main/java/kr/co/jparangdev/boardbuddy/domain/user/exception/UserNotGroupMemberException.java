package kr.co.jparangdev.boardbuddy.domain.user.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ForbiddenException;
import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;

public class UserNotGroupMemberException extends ForbiddenException implements MessageResolvable {

    private final Long groupId;
    private final Long userId;

    public UserNotGroupMemberException(Long groupId, Long userId) {
        super(UserErrorCode.USER_NOT_GROUP_MEMBER, "User " + userId + " is not a member of group " + groupId);
        this.groupId = groupId;
        this.userId = userId;
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[]{userId, groupId};
    }
}
