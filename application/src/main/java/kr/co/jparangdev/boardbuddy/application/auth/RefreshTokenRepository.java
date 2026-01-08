package kr.co.jparangdev.boardbuddy.application.auth;

import java.util.Optional;

public interface RefreshTokenRepository {
    void save(String token, Long userId);
    Optional<Long> findUserIdByToken(String token);
    void delete(String token);
}
