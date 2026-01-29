package kr.co.jparangdev.boardbuddy.persistence.user;

import kr.co.jparangdev.boardbuddy.application.user.service.UserRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Random;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;
    private final Random random = new Random();

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        UserJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
        return jpaRepository.findByProviderAndProviderId(provider, providerId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByNicknameAndDiscriminator(String nickname, String discriminator) {
        return jpaRepository.findByNicknameAndDiscriminator(nickname, discriminator)
            .map(mapper::toDomain);
    }

    @Override
    public String generateUniqueDiscriminator(String nickname) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        for (int attempt = 0; attempt < 100; attempt++) {
            StringBuilder sb = new StringBuilder(4);
            for (int i = 0; i < 4; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            String discriminator = sb.toString();

            if (!jpaRepository.findByNicknameAndDiscriminator(nickname, discriminator).isPresent()) {
                return discriminator;
            }
        }

        // Fallback: 타임스탬프 기반
        long timestamp = System.currentTimeMillis() % 1296; // 36^2
        return String.format("%c%c%02d",
            chars.charAt((int)(timestamp / 36)),
            chars.charAt((int)(timestamp % 36)),
            random.nextInt(100));
    }
}
