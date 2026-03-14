package kr.co.jparangdev.boardbuddy.application.group.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ForbiddenException;
import kr.co.jparangdev.boardbuddy.domain.group.exception.GroupErrorCode;

public class NotGroupOwnerException extends ForbiddenException {

    public NotGroupOwnerException(Long groupId, Long userId) {
        super(GroupErrorCode.NOT_GROUP_OWNER, "User " + userId + " is not the owner of group " + groupId);
    }
}
