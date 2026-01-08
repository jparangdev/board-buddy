package kr.co.jparangdev.boardbuddy.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import kr.co.jparangdev.boardbuddy.application.auth.AuthTokens;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthDto {

    @Getter
    @AllArgsConstructor
    public static class AuthUrlResponse {
        private String authorizationUrl;
    }

    @Getter
    @Setter
    public static class CallbackRequest {
        @NotBlank
        private String code;

        @NotBlank
        private String state;
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
