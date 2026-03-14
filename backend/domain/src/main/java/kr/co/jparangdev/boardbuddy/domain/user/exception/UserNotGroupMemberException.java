package kr.co.jparangdev.boardbuddy.domain.user.exception;

import kr.co.jparangdev.boardbuddy.domain.user.exception.UserErrorCode;
import kr.co.jparangdev.boardbuddy.domain.exception.ForbiddenException;

public class UserNotGroupMemberException extends ForbiddenException {

    public UserNotGroupMemberException(Long groupId, Long userId) {
        super(UserErrorCode.USER_NOT_GROUP_MEMBER, "User " + userId + " is not a member of group " + groupId);
    }
}
