package kr.co.jparangdev.boardbuddy.application.group.exception;

public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException(Long groupId) {
        super("Group not found: " + groupId);
    }
}
