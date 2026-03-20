package kr.co.jparangdev.boardbuddy.domain.group.exception;


import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCode;

public enum GroupErrorCode implements ErrorCode {

    GROUP_NOT_FOUND,
    NOT_GROUP_OWNER;
}
