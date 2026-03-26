package kr.co.jparangdev.boardbuddy.domain.group.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;
import kr.co.jparangdev.boardbuddy.domain.exception.NotFoundException;

@SuppressWarnings("java:S110")
public class GroupNotFoundException extends NotFoundException implements MessageResolvable {

    private final Long groupId;

    public GroupNotFoundException(Long groupId) {
        super(GroupErrorCode.GROUP_NOT_FOUND, "Group not found: " + groupId);
        this.groupId = groupId;
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[]{groupId};
    }
}
