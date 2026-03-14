package kr.co.jparangdev.boardbuddy.application.group.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.NotFoundException;
import kr.co.jparangdev.boardbuddy.domain.group.exception.GroupErrorCode;

public class GroupNotFoundException extends NotFoundException {

    public GroupNotFoundException(Long groupId) {
        super(GroupErrorCode.GROUP_NOT_FOUND, "Group not found: " + groupId);
    }
}
