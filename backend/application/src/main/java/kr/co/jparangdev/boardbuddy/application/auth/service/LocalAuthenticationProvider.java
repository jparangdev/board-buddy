package kr.co.jparangdev.boardbuddy.application.auth.service;

import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthCredentials;
import kr.co.jparangdev.boardbuddy.application.auth.dto.LocalAuthCredentials;
import kr.co.jparangdev.boardbuddy.application.auth.exception.InvalidCredentialsException;
import kr.co.jparangdev.boardbuddy.domain.auth.ProviderType;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ProviderType getProviderType() {
        return ProviderType.LOCAL;
    }

    @Override
    public User authenticate(AuthCredentials credentials) {
        if (!(credentials instanceof LocalAuthCredentials localCredentials)) {
            throw new IllegalArgumentException("Invalid credentials type for LOCAL provider");
        }

        String providerName = ProviderType.LOCAL.name();
        User user = userRepository.findByProviderAndProviderId(providerName, localCredentials.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(localCredentials.rawPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }
}
