package kr.co.jparangdev.boardbuddy.application.user.usecase;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddy.domain.user.User;

public interface UserQueryUseCase {
    Optional<User> getUserById(Long id);

    User getCurrentUser();

    List<User> searchUsers(String keyword);
}
