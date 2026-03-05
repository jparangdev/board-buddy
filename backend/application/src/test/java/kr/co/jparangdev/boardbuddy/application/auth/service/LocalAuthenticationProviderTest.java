package kr.co.jparangdev.boardbuddy.application.auth.service;

import kr.co.jparangdev.boardbuddy.application.auth.dto.LocalAuthCredentials;
import kr.co.jparangdev.boardbuddy.application.auth.exception.InvalidCredentialsException;
import kr.co.jparangdev.boardbuddy.domain.auth.ProviderType;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LocalAuthenticationProviderTest {

    @InjectMocks
    private LocalAuthenticationProvider provider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Returns LOCAL provider type")
    void getProviderType() {
        assertThat(provider.getProviderType()).isEqualTo(ProviderType.LOCAL);
    }

    @Test
    @DisplayName("Authenticate succeeds with valid credentials")
    void authenticate_success() {
        User user = User.createLocal("alice@test.com", "hashed-pw", "Alice", "AB12");
        given(userRepository.findByProviderAndProviderId("LOCAL", "alice@test.com"))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches("raw-pw", "hashed-pw")).willReturn(true);

        User result = provider.authenticate(new LocalAuthCredentials("alice@test.com", "raw-pw"));

        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("Throws InvalidCredentialsException when user not found")
    void authenticate_userNotFound() {
        given(userRepository.findByProviderAndProviderId("LOCAL", "unknown@test.com"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                provider.authenticate(new LocalAuthCredentials("unknown@test.com", "any-pw")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("Throws InvalidCredentialsException when password does not match")
    void authenticate_wrongPassword() {
        User user = User.createLocal("alice@test.com", "hashed-pw", "Alice", "AB12");
        given(userRepository.findByProviderAndProviderId("LOCAL", "alice@test.com"))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong-pw", "hashed-pw")).willReturn(false);

        assertThatThrownBy(() ->
                provider.authenticate(new LocalAuthCredentials("alice@test.com", "wrong-pw")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
