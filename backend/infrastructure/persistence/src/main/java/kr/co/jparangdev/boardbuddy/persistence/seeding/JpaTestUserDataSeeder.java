package kr.co.jparangdev.boardbuddy.persistence.seeding;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.seeding.port.UserDataSeeder;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Infrastructure adapter for seeding test user data.
 * Only active in local and dev profiles.
 * Implements the UserDataSeeder port defined in application layer.
 * Test users are created as LOCAL users with password: Test1234!
 */
@Slf4j
@Component
@Profile({"local", "dev"})
@RequiredArgsConstructor
public class JpaTestUserDataSeeder implements UserDataSeeder {

    static final String TEST_PASSWORD = "Test1234!";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void seed() {
        List<TestUser> testUsers = List.of(
            new TestUser("test@test.com",    "Tester",    "TST1"),
            new TestUser("player1@test.com", "PlayerOne", "PLY1"),
            new TestUser("player2@test.com", "PlayerTwo", "PLY2"),
            new TestUser("player3@test.com", "PlayerThree", "PLY3"),
            new TestUser("player4@test.com", "PlayerFour", "PLY4")
        );

        String passwordHash = passwordEncoder.encode(TEST_PASSWORD);
        int created = 0;

        for (TestUser testUser : testUsers) {
            boolean exists = userRepository.findByNicknameAndDiscriminator(testUser.nickname(), testUser.discriminator()).isPresent()
                || userRepository.findByEmail(testUser.email()).isPresent();

            if (!exists) {
                User newUser = User.createLocal(
                    testUser.email(),
                    passwordHash,
                    testUser.nickname(),
                    testUser.discriminator()
                );
                userRepository.save(newUser);
                created++;
                log.info("Created test user: {}#{} ({})", testUser.nickname(), testUser.discriminator(), testUser.email());
            }
        }

        if (created > 0) {
            log.info("Seeded {} test users", created);
        } else {
            log.debug("Test users already exist, skipping creation");
        }
    }

    private record TestUser(String email, String nickname, String discriminator) {}
}
