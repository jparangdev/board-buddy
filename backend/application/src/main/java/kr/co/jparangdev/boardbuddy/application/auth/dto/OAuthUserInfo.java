package kr.co.jparangdev.boardbuddy.application.auth.dto;

public record OAuthUserInfo(
        String externalId,
        String email,
        String nickname
) {
}
