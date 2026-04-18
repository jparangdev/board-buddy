package kr.co.jparangdev.boardbuddy.api.user;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.api.user.dto.UserDto;
import kr.co.jparangdev.boardbuddy.domain.user.User;

@Component
public class UserDtoMapper {

    public UserDto.Response toResponse(User user) {
        return UserDto.Response.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .discriminator(user.getDiscriminator())
            .userTag(user.getUserTag())
            .provider(user.getProvider())
            .build();
    }

    public UserDto.SearchResponse toSearchResponse(List<User> users) {
        return UserDto.SearchResponse.builder()
            .users(users.stream()
                .map(this::toResponse)
                .toList())
            .build();
    }
}
