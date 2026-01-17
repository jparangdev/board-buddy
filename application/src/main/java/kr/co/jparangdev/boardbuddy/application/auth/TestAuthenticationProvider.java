package kr.co.jparangdev.boardbuddy.application.auth;

import kr.co.jparangdev.boardbuddy.application.user.UserRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Test authentication provider for development and testing.
 * Creates or retrieves users based on email without external OAuth.
 */
@Component
@RequiredArgsConstructor
public class TestAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    @Override
    public ProviderType getProviderType() {
        return ProviderType.TEST;
    }

    @Override
    public User authenticate(AuthCredentials credentials) {
        if (!(credentials instanceof TestAuthCredentials testCredentials)) {
            throw new IllegalArgumentException("Invalid credentials type for TEST provider");
        }

        String email = testCredentials.getEmail();
        String nickname = testCredentials.getNickname();

        // Find existing user or create new one
        String providerName = ProviderType.TEST.name();
        return userRepository.findByProviderAndProviderId(providerName, email)
            .orElseGet(() -> createNewUser(email, nickname));
    }

    private User createNewUser(String email, String nickname) {
        String effectiveNickname = (nickname != null && !nickname.isBlank())
            ? nickname
            : email.split("@")[0];

        String discriminator = userRepository.generateUniqueDiscriminator(effectiveNickname);

        User newUser = User.fromOAuth(
            email,
            ProviderType.TEST.name(),
            email, // Use email as providerId for test auth
            effectiveNickname,
            discriminator
        );

        return userRepository.save(newUser);
    }
}
