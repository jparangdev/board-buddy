package kr.co.jparangdev.boardbuddies.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import kr.co.jparangdev.boardbuddies.application.repostory.UserRepository;
import kr.co.jparangdev.boardbuddies.application.usecases.UserManagementUseCase;
import kr.co.jparangdev.boardbuddies.domain.entity.User;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of the UserManagementUseCase interface
 */
@RequiredArgsConstructor
@Service
public class UserManagementService implements UserManagementUseCase {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.update(
                            userDetails.getUsername(),
                            userDetails.getEmail(),
                            userDetails.getNickname()
                    );
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
