package kr.co.jparangdev.boardbuddy.api.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import kr.co.jparangdev.boardbuddy.application.auth.exception.DuplicateEmailException;
import kr.co.jparangdev.boardbuddy.application.auth.exception.InvalidCredentialsException;
import kr.co.jparangdev.boardbuddy.application.auth.exception.InvalidTokenException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("InvalidCredentialsException returns 401 INVALID_CREDENTIALS")
    void handleInvalidCredentials_returns_401() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleInvalidCredentials(new InvalidCredentialsException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getError()).isEqualTo("INVALID_CREDENTIALS");
        assertThat(response.getBody().getMessage()).isNotBlank();
        assertThat(response.getBody().getFieldErrors()).isNull();
    }

    @Test
    @DisplayName("DuplicateEmailException returns 409 DUPLICATE_EMAIL")
    void handleDuplicateEmail_returns_409() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleDuplicateEmail(new DuplicateEmailException("test@example.com"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError()).isEqualTo("DUPLICATE_EMAIL");
        assertThat(response.getBody().getMessage()).contains("test@example.com");
        assertThat(response.getBody().getFieldErrors()).isNull();
    }

    @Test
    @DisplayName("InvalidTokenException returns 401 INVALID_TOKEN")
    void handleInvalidToken_returns_401() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleInvalidToken(new InvalidTokenException("expired"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getError()).isEqualTo("INVALID_TOKEN");
    }

    @Test
    @DisplayName("MethodArgumentNotValidException returns 400 with fieldErrors map")
    void handleValidationError_returns_400_with_field_errors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        List<FieldError> fieldErrors = List.of(
                new FieldError("registerRequest", "password", "Password must be at least 8 characters"),
                new FieldError("registerRequest", "email", "Invalid email format")
        );

        given(ex.getBindingResult()).willReturn(bindingResult);
        given(bindingResult.getFieldErrors()).willReturn(fieldErrors);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleValidationError(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getFieldErrors())
                .containsEntry("password", "Password must be at least 8 characters")
                .containsEntry("email", "Invalid email format");
    }

    @Test
    @DisplayName("MethodArgumentNotValidException with no field errors returns 400 with empty map")
    void handleValidationError_no_fields_returns_empty_map() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        given(ex.getBindingResult()).willReturn(bindingResult);
        given(bindingResult.getFieldErrors()).willReturn(List.of());

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleValidationError(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getFieldErrors()).isEmpty();
    }
}
