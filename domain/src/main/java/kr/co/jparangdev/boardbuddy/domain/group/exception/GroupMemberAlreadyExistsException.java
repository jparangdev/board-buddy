package kr.co.jparangdev.boardbuddy.domain.group.exception;

public class GroupMemberAlreadyExistsException extends RuntimeException {
    public GroupMemberAlreadyExistsException(Long groupId, Long userId) {
        super("User " + userId + " is already a member of group " + groupId);
    }

    public GroupMemberAlreadyExistsException(String userTag) {
        super("User " + userTag + " is already a member of this group");
    }
}
