package kr.co.jparangdev.boardbuddy.application.user.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ForbiddenException;
import kr.co.jparangdev.boardbuddy.domain.user.exception.UserErrorCode;

public class UserNotGroupMemberException extends ForbiddenException {

    public UserNotGroupMemberException(Long groupId, Long userId) {
        super(UserErrorCode.USER_NOT_GROUP_MEMBER, "User " + userId + " is not a member of group " + groupId);
    }
}
