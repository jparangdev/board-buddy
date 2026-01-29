package kr.co.jparangdev.boardbuddy.application.user.service;

import kr.co.jparangdev.boardbuddy.application.user.exception.UserNotFoundException;
import kr.co.jparangdev.boardbuddy.application.user.usecase.UserManagementUseCase;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserManagementService implements UserManagementUseCase {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User getCurrentUser() {
        Long userId = (Long) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
