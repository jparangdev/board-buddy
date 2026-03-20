package kr.co.jparangdev.boardbuddy.application.auth.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import kr.co.jparangdev.boardbuddy.domain.auth.exception.DuplicateEmailException;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @InjectMocks
    private UserRegistrationService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Register succeeds with new email")
    void register_success() {
        given(userRepository.findByEmail("alice@test.com")).willReturn(Optional.empty());
        given(passwordEncoder.encode("rawPass1")).willReturn("hashed");
        given(userRepository.generateUniqueDiscriminator("Alice")).willReturn("AB12");
        given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        service.register("alice@test.com", "rawPass1", "Alice");

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Throws DuplicateEmailException when email already exists")
    void register_duplicateEmail() {
        User existing = User.createLocal("alice@test.com", "hashed", "Alice", "AB12");
        given(userRepository.findByEmail("alice@test.com")).willReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.register("alice@test.com", "rawPass1", "Alice"))
                .isInstanceOf(DuplicateEmailException.class);
    }
}
