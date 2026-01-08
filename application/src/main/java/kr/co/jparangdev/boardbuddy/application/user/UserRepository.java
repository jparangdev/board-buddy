package kr.co.jparangdev.boardbuddy.application.user;

import kr.co.jparangdev.boardbuddy.domain.user.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    String generateUniqueDiscriminator(String nickname);
}
