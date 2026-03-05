package kr.co.jparangdev.boardbuddy.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthTokens;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthDto {

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
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 8)
        private String password;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;

        @NotBlank(message = "Nickname is required")
        @Size(min = 2, max = 50, message = "Nickname must be between 2 and 50 characters")
        private String nickname;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RefreshRequest {
        @NotBlank
        private String refreshToken;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LogoutRequest {
        @NotBlank
        private String refreshToken;
    }
}
