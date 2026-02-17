package kr.co.jparangdev.boardbuddy.application.seeding.port;

/**
 * Port for seeding test user data.
 * Infrastructure layer provides the implementation.
 */
public interface UserDataSeeder {

    /**
     * Seed test user data into the database.
     * Implementation should be idempotent (safe to run multiple times).
     */
    void seed();
}
