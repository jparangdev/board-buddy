package kr.co.jparangdev.boardbuddy.application.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User not found: " + userId);
    }

    public UserNotFoundException(String userTag) {
        super("User not found: " + userTag);
    }
}
