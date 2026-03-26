package kr.co.jparangdev.boardbuddy.api.exception;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import kr.co.jparangdev.boardbuddy.domain.exception.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @Getter
    public static class ErrorResponse {
        private final String error;
        private final String message;
        private final Map<String, String> fieldErrors;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
            this.fieldErrors = null;
        }

        public ErrorResponse(String error, String message, Map<String, String> fieldErrors) {
            this.error = error;
            this.message = message;
            this.fieldErrors = fieldErrors;
        }
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e, Locale locale) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(e.getErrorCode().name(), resolveMessage(e, locale)));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException e, Locale locale) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getErrorCode().name(), resolveMessage(e, locale)));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthException e, Locale locale) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(e.getErrorCode().name(), resolveMessage(e, locale)));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException e, Locale locale) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(e.getErrorCode().name(), resolveMessage(e, locale)));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException e, Locale locale) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(e.getErrorCode().name(), resolveMessage(e, locale)));
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ErrorResponse> handleInfrastructure(InfrastructureException e) {
        log.error("[{}] {}", e.getErrorCode(), e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(e.getErrorCode().name(), "An unexpected error occurred"));
    }

    @ExceptionHandler(BoardBuddyException.class)
    public ResponseEntity<ErrorResponse> handleBoardBuddy(BoardBuddyException e) {
        log.error("Unhandled BoardBuddyException [{}] {}", e.getErrorCode(), e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(e.getErrorCode().name(), "An unexpected error occurred"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException e, Locale locale) {
        Locale resolved = locale != null ? locale : LocaleContextHolder.getLocale();
        Map<String, String> fieldErrors = e.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage,
                (first, second) -> first
            ));
        String message = messageSource.getMessage("VALIDATION_ERROR", null, "Invalid request", resolved);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("VALIDATION_ERROR", message, fieldErrors));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"));
    }

    private String resolveMessage(BoardBuddyException e, Locale locale) {
        Locale resolved = locale != null ? locale : LocaleContextHolder.getLocale();
        if (e instanceof MessageResolvable mr) {
            return messageSource.getMessage(mr.getMessageKey(), mr.getMessageArgs(), e.getMessage(), resolved);
        }
        return e.getMessage();
    }
}
