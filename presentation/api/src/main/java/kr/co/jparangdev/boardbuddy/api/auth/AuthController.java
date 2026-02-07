package kr.co.jparangdev.boardbuddy.api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddy.api.auth.dto.AuthDto;
import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthTokens;
import kr.co.jparangdev.boardbuddy.application.auth.usecase.AuthenticationUseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;

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
