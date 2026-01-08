package kr.co.jparangdev.boardbuddy.api.user;

import kr.co.jparangdev.boardbuddy.api.user.dto.UserDto;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public UserDto.Response toResponse(User user) {
        return UserDto.Response.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .discriminator(user.getDiscriminator())
            .userTag(user.getUserTag())
            .build();
    }
}
