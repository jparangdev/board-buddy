package kr.co.jparangdev.boardbuddy.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.domain.exception.ErrorCategory;

@Component
public class ErrorCategoryHttpStatusMapper {

    public HttpStatus resolve(ErrorCategory category) {
        return switch (category) {
            case NOT_FOUND    -> HttpStatus.NOT_FOUND;
            case CONFLICT     -> HttpStatus.CONFLICT;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN    -> HttpStatus.FORBIDDEN;
            case VALIDATION   -> HttpStatus.BAD_REQUEST;
            case INTERNAL     -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
