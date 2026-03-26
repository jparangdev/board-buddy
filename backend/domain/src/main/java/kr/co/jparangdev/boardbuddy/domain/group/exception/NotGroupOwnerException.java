package kr.co.jparangdev.boardbuddy.domain.group.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ForbiddenException;
import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;

@SuppressWarnings("java:S110")
public class NotGroupOwnerException extends ForbiddenException implements MessageResolvable {

    private final Long groupId;
    private final Long userId;

    public NotGroupOwnerException(Long groupId, Long userId) {
        super(GroupErrorCode.NOT_GROUP_OWNER, "User " + userId + " is not the owner of group " + groupId);
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
