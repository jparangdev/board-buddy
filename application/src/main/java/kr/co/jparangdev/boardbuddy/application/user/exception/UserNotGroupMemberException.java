package kr.co.jparangdev.boardbuddy.application.user.exception;

public class UserNotGroupMemberException extends RuntimeException {
    public UserNotGroupMemberException(Long groupId, Long userId) {
        super("User " + userId + " is not a member of group " + groupId);
    }
}
