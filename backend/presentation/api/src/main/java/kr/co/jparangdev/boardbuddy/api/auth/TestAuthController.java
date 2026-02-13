package kr.co.jparangdev.boardbuddy.api.auth;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddy.api.auth.dto.AuthDto;
import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthTokens;
import kr.co.jparangdev.boardbuddy.application.auth.dto.TestAuthCredentials;
import kr.co.jparangdev.boardbuddy.application.auth.usecase.AuthenticationUseCase;
import lombok.RequiredArgsConstructor;

/**
 * Test authentication controller that is only available in local and dev profiles.
 * This controller provides a simple email-based login without password for development purposes.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class TestAuthController {

    private final AuthenticationUseCase authenticationUseCase;

    /**
     * Test login for development/testing.
     * Only available in local and dev profiles.
     */
    @PostMapping("/test/login")
    public ResponseEntity<AuthDto.TokenResponse> testLogin(
            @Valid @RequestBody AuthDto.TestLoginRequest request) {
        TestAuthCredentials credentials = new TestAuthCredentials(
            request.getEmail(),
            request.getNickname()
        );
        AuthTokens tokens = authenticationUseCase.authenticate(credentials);
        return ResponseEntity.ok(AuthDto.TokenResponse.from(tokens));
    }
}
