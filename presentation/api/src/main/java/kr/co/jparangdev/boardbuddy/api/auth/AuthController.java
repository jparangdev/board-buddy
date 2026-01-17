package kr.co.jparangdev.boardbuddy.api.auth;

import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddy.api.auth.dto.AuthDto;
import kr.co.jparangdev.boardbuddy.application.auth.AuthTokens;
import kr.co.jparangdev.boardbuddy.application.auth.AuthenticationUseCase;
import kr.co.jparangdev.boardbuddy.application.auth.TestAuthCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;

    /**
     * Test login for development/testing.
     * To add OAuth providers later, add new endpoints like:
     * - POST /api/v1/auth/kakao/callback
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

    /**
     * Refresh access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthDto.TokenResponse> refreshToken(
            @Valid @RequestBody AuthDto.RefreshRequest request) {
        AuthTokens tokens = authenticationUseCase.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(AuthDto.TokenResponse.from(tokens));
    }

    /**
     * Logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody AuthDto.LogoutRequest request) {
        authenticationUseCase.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
