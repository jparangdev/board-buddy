package kr.co.jparangdev.boardbuddy.domain.user;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    private Long id;
    private String email;              // 불변
    private String nickname;
    private String discriminator;      // 불변 - 문자포함 4자리 (예: A1B2)
    private String provider;           // "NAVER"
    private String providerId;

    @Builder
    public User(Long id, String email, String nickname, String discriminator,
                String provider, String providerId) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.discriminator = discriminator;
        this.provider = provider;
        this.providerId = providerId;
    }

    public String getUserTag() {
        return nickname + "#" + discriminator;
    }

    public static User fromOAuth(String email, String provider, String providerId,
                                 String initialNickname, String discriminator) {
        return User.builder()
                .email(email)
                .provider(provider)
                .providerId(providerId)
                .nickname(initialNickname)
                .discriminator(discriminator)
                .build();
    }
}
