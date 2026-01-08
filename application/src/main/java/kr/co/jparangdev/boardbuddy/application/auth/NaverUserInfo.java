package kr.co.jparangdev.boardbuddy.application.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverUserInfo {
    @JsonProperty("id")
    private String id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("name")
    private String name;
}
