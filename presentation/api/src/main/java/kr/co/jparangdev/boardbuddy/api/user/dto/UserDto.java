package kr.co.jparangdev.boardbuddy.api.user.dto;

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
    }
}
