package kr.co.jparangdev.boardbuddy.domain.user.repository;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddy.domain.user.User;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    Optional<User> findByNicknameAndDiscriminator(String nickname, String discriminator);
    String generateUniqueDiscriminator(String nickname);
    List<User> searchByNicknameContaining(String keyword, int limit);
    boolean existsById(Long id);
    void deleteById(Long id);
}
