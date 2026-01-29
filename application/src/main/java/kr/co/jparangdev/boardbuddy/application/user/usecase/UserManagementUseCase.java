package kr.co.jparangdev.boardbuddy.application.user.usecase;

import kr.co.jparangdev.boardbuddy.domain.user.User;

import java.util.Optional;

public interface UserManagementUseCase {
    /**
     * Get user by ID
     */
    Optional<User> getUserById(Long id);

    /**
     * Get current authenticated user
     */
    User getCurrentUser();
}
