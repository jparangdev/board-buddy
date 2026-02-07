package kr.co.jparangdev.boardbuddy.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import kr.co.jparangdev.boardbuddy.application.auth.exception.InvalidTokenException;
import kr.co.jparangdev.boardbuddy.application.auth.exception.OAuthAuthenticationException;
import kr.co.jparangdev.boardbuddy.application.group.exception.GroupNotFoundException;
import kr.co.jparangdev.boardbuddy.application.group.exception.NotGroupOwnerException;
import kr.co.jparangdev.boardbuddy.application.user.exception.UserNotFoundException;
import kr.co.jparangdev.boardbuddy.application.user.exception.UserNotGroupMemberException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private String error;
        private String message;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("INVALID_TOKEN", e.getMessage()));
    }

    @ExceptionHandler(OAuthAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleOAuthError(OAuthAuthenticationException e) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("OAUTH_AUTHENTICATION_FAILED", e.getMessage()));
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGroupNotFound(GroupNotFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("GROUP_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(NotGroupOwnerException.class)
    public ResponseEntity<ErrorResponse> handleNotGroupOwner(NotGroupOwnerException e) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("NOT_GROUP_OWNER", e.getMessage()));
    }

    @ExceptionHandler(UserNotGroupMemberException.class)
    public ResponseEntity<ErrorResponse> handleUserNotGroupMember(UserNotGroupMemberException e) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("USER_NOT_GROUP_MEMBER", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("INVALID_ARGUMENT", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
