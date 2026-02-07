package kr.co.jparangdev.boardbuddy.application.user.usecase;

import java.util.Optional;

import kr.co.jparangdev.boardbuddy.domain.user.User;

public interface GetUserByIdUseCase {
    Optional<User> getUserById(Long id);
}
