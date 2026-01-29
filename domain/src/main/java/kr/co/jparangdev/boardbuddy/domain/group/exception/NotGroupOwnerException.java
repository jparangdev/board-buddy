package kr.co.jparangdev.boardbuddy.domain.group.exception;

public class NotGroupOwnerException extends RuntimeException {
    public NotGroupOwnerException(Long groupId, Long userId) {
        super("User " + userId + " is not the owner of group " + groupId);
    }
}
