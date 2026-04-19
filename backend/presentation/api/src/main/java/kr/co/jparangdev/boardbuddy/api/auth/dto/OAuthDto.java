package kr.co.jparangdev.boardbuddy.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuthDto {

    @Getter
    @Builder
    public static class AuthorizeUrlResponse {
        private String authorizeUrl;

        public static AuthorizeUrlResponse of(String url) {
            return AuthorizeUrlResponse.builder().authorizeUrl(url).build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OAuthLoginRequest {
        @NotBlank
        private String code;

        @NotBlank
        private String redirectUri;
    }
}
