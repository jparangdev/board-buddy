package kr.co.jparangdev.boardbuddy.domain.user.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND,
    USER_NOT_GROUP_MEMBER;
}
