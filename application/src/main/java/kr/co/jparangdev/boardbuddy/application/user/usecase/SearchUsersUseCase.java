package kr.co.jparangdev.boardbuddy.application.user.usecase;

import java.util.List;

import kr.co.jparangdev.boardbuddy.domain.user.User;

public interface SearchUsersUseCase {
    List<User> searchUsers(String keyword);
}
