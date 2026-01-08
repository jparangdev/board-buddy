package kr.co.jparangdev.boardbuddy.transients.auth;

import kr.co.jparangdev.boardbuddy.application.auth.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private static final String KEY_PREFIX = "refresh_token:";
    private static final Duration TTL = Duration.ofDays(30);

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(String token, Long userId) {
        String key = KEY_PREFIX + token;
        redisTemplate.opsForValue().set(key, userId, TTL);
    }

    @Override
    public Optional<Long> findUserIdByToken(String token) {
        String key = KEY_PREFIX + token;
        Object userId = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(userId)
            .map(id -> ((Number) id).longValue());
    }

    @Override
    public void delete(String token) {
        String key = KEY_PREFIX + token;
        redisTemplate.delete(key);
    }
}
