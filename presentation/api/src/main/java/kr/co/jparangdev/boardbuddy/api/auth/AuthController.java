package kr.co.jparangdev.boardbuddy.api.auth;

import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddy.api.auth.dto.AuthDto;
import kr.co.jparangdev.boardbuddy.application.auth.AuthTokens;
import kr.co.jparangdev.boardbuddy.application.auth.AuthenticationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;

    /**
     * Get Naver OAuth authorization URL
     */
    @GetMapping("/naver")
    public ResponseEntity<AuthDto.AuthUrlResponse> getNaverAuthUrl() {
        String authUrl = authenticationUseCase.getNaverAuthorizationUrl();
        return ResponseEntity.ok(new AuthDto.AuthUrlResponse(authUrl));
    }

    /**
     * Handle Naver OAuth callback
     */
    @PostMapping("/naver/callback")
    public ResponseEntity<AuthDto.TokenResponse> naverCallback(
            @Valid @RequestBody AuthDto.CallbackRequest request) {
        AuthTokens tokens = authenticationUseCase.processNaverCallback(
            request.getCode(),
            request.getState()
        );
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
