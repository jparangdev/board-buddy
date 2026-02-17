package kr.co.jparangdev.boardbuddy.application.seeding.port;

/**
 * Port for seeding official game data.
 * Infrastructure layer provides the implementation.
 */
public interface GameDataSeeder {

    /**
     * Seed official board game data into the database.
     * Implementation should be idempotent (safe to run multiple times).
     */
    void seed();
}
