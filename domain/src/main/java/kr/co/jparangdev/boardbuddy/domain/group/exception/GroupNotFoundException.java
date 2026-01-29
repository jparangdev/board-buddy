package kr.co.jparangdev.boardbuddy.domain.group.exception;

public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException(Long groupId) {
        super("Group not found: " + groupId);
    }
}
