package kr.co.jparangdev.boardbuddy.application.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthTokens {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String tokenType;
}
