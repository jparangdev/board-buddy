package kr.co.jparangdev.boardbuddy.api.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddy.api.auth.dto.AuthDto;
import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthTokens;
import kr.co.jparangdev.boardbuddy.application.auth.dto.LocalAuthCredentials;
import kr.co.jparangdev.boardbuddy.application.auth.usecase.AuthenticationUseCase;
import kr.co.jparangdev.boardbuddy.application.auth.usecase.RegisterUseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;
    private final RegisterUseCase registerUseCase;

    /**
     * Login with email and password
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDto.TokenResponse> login(
            @Valid @RequestBody AuthDto.LoginRequest request) {
        AuthTokens tokens = authenticationUseCase.authenticate(
                new LocalAuthCredentials(request.getEmail(), request.getPassword()));
        return ResponseEntity.ok(AuthDto.TokenResponse.from(tokens));
    }

    /**
     * Register a new account with email and password
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody AuthDto.RegisterRequest request) {
        registerUseCase.register(request.getEmail(), request.getPassword(), request.getNickname());
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
