package kr.co.jparangdev.boardbuddy.application.user.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.NotFoundException;
import kr.co.jparangdev.boardbuddy.domain.user.exception.UserErrorCode;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(Long userId) {
        super(UserErrorCode.USER_NOT_FOUND, "User not found: " + userId);
    }

    public UserNotFoundException(String userTag) {
        super(UserErrorCode.USER_NOT_FOUND, "User not found: " + userTag);
    }
}
