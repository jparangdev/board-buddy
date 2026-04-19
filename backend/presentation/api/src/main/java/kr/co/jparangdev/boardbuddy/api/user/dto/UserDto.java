package kr.co.jparangdev.boardbuddy.api.user.dto;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDto {

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String email;
        private String nickname;
        private String discriminator;
        private String userTag;
        private String provider;
    }

    @Getter
    @Builder
    public static class SearchResponse {
        private List<Response> users;
    }

    @Getter
    public static class UpdateNicknameRequest {
        @NotBlank
        @Size(min = 2, max = 50)
        private String nickname;
    }

    @Getter
    @Builder
    public static class SocialAccountResponse {
        private String provider;
        private Instant linkedAt;
    }

    @Getter
    @Builder
    public static class SocialAccountListResponse {
        private List<SocialAccountResponse> accounts;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkAccountRequest {
        @NotBlank
        private String code;

        @NotBlank
        private String redirectUri;
    }
}
