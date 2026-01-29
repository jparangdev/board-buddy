package kr.co.jparangdev.boardbuddy.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthTokens;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthDto {

    /**
     * Test login request for development/testing
     */
    @Getter
    @Setter
    public static class TestLoginRequest {
        @NotBlank
        @Email
        private String email;

        private String nickname; // Optional - will use email prefix if not provided
    }

    @Getter
    @Builder
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private Long expiresIn;
        private String tokenType;

        public static TokenResponse from(AuthTokens tokens) {
            return TokenResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .expiresIn(tokens.getExpiresIn())
                .tokenType(tokens.getTokenType())
                .build();
        }
    }

    @Getter
    @Setter
    public static class RefreshRequest {
        @NotBlank
        private String refreshToken;
    }

    @Getter
    @Setter
    public static class LogoutRequest {
        @NotBlank
        private String refreshToken;
    }
}
