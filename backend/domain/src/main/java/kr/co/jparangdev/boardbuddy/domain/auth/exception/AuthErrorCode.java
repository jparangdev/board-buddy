package kr.co.jparangdev.boardbuddy.domain.auth.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

    INVALID_CREDENTIALS,
    INVALID_TOKEN,
    OAUTH_AUTHENTICATION_FAILED,
    DUPLICATE_EMAIL,
    PROVIDER_ALREADY_LINKED,
    CANNOT_UNLINK_LAST_METHOD,
    UNSUPPORTED_PROVIDER;
}
