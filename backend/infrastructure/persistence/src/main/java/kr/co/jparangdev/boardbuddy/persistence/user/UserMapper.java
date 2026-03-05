package kr.co.jparangdev.boardbuddy.persistence.user;

import kr.co.jparangdev.boardbuddy.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getDiscriminator(),
            user.getProvider(),
            user.getProviderId(),
            user.getPasswordHash()
        );
    }

    public User toDomain(UserJpaEntity entity) {
        return User.builder()
            .id(entity.getId())
            .email(entity.getEmail())
            .nickname(entity.getNickname())
            .discriminator(entity.getDiscriminator())
            .provider(entity.getProvider())
            .providerId(entity.getProviderId())
            .passwordHash(entity.getPasswordHash())
            .build();
    }
}
