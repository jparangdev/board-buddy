package kr.co.jparangdev.boardbuddy.application.auth.service;

import kr.co.jparangdev.boardbuddy.application.auth.exception.DuplicateEmailException;
import kr.co.jparangdev.boardbuddy.application.auth.usecase.RegisterUseCase;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserRegistrationService implements RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(String email, String rawPassword, String nickname) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException(email);
        }

        String passwordHash = passwordEncoder.encode(rawPassword);
        String discriminator = userRepository.generateUniqueDiscriminator(nickname);
        User user = User.createLocal(email, passwordHash, nickname, discriminator);
        userRepository.save(user);
    }
}
