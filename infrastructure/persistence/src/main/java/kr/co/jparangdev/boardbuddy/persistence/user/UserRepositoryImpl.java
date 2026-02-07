package kr.co.jparangdev.boardbuddy.persistence.user;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;
    private final SecureRandom random = new SecureRandom();

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
    public List<User> searchByNicknameContaining(String keyword, int limit) {
        return jpaRepository.findByNicknameContainingIgnoreCase(keyword).stream()
            .limit(limit)
            .map(mapper::toDomain)
            .toList();
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

            if (jpaRepository.findByNicknameAndDiscriminator(nickname, discriminator).isEmpty()) {
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
