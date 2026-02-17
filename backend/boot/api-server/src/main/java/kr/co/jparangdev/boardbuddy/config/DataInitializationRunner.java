package kr.co.jparangdev.boardbuddy.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.application.seeding.port.GameDataSeeder;
import kr.co.jparangdev.boardbuddy.application.seeding.port.UserDataSeeder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Triggers data seeding on application startup.
 * This class has NO business logic - it only coordinates seeders.
 *
 * Responsibilities:
 * - Wire seeders via dependency injection
 * - Trigger seeding in correct order
 * - Handle errors gracefully
 *
 * Business logic is in:
 * - Ports (application layer): GameDataSeeder, UserDataSeeder
 * - Adapters (infrastructure layer): JpaGameDataSeeder, JpaTestUserDataSeeder
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DataInitializationRunner implements ApplicationRunner {

    private final GameDataSeeder gameDataSeeder;
    private final List<UserDataSeeder> userDataSeeders; // List to handle optional beans

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting data initialization...");

        try {
            // Seed official game data (all profiles)
            gameDataSeeder.seed();

            // Seed test users (local/dev profiles only)
            userDataSeeders.forEach(UserDataSeeder::seed);

            log.info("Data initialization completed successfully");
        } catch (Exception e) {
            log.error("Data initialization failed: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to initialize data", e);
        }
    }
}
