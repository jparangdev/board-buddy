package kr.co.jparangdev.boardbuddy.api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddy.api.auth.dto.AuthDto;
import kr.co.jparangdev.boardbuddy.api.auth.dto.OAuthDto;
import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthTokens;
import kr.co.jparangdev.boardbuddy.application.auth.usecase.OAuthLoginUseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthLoginUseCase oAuthLoginUseCase;

    @GetMapping("/{provider}/authorize-url")
    public ResponseEntity<OAuthDto.AuthorizeUrlResponse> getAuthorizeUrl(
            @PathVariable("provider") String provider,
            @RequestParam("redirectUri") String redirectUri) {
        String url = oAuthLoginUseCase.getAuthorizationUrl(provider, redirectUri);
        return ResponseEntity.ok(OAuthDto.AuthorizeUrlResponse.of(url));
    }

    @PostMapping("/{provider}/login")
    public ResponseEntity<AuthDto.TokenResponse> loginWithOAuth(
            @PathVariable("provider") String provider,
            @Valid @RequestBody OAuthDto.OAuthLoginRequest request) {
        AuthTokens tokens = oAuthLoginUseCase.loginWithOAuth(
                provider, request.getCode(), request.getRedirectUri());
        return ResponseEntity.ok(AuthDto.TokenResponse.from(tokens));
    }
}
