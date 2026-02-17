package kr.co.jparangdev.boardbuddy.application.cleaning.port;

/**
 * Port for cleaning test data in development environment.
 * Infrastructure layer provides the implementation.
 */
public interface TestDataCleaner {

    /**
     * Reset all test data (sessions, groups, temp users).
     */
    void resetAll();

    /**
     * Clear all game sessions and their results.
     */
    void clearAllGameSessions();

    /**
     * Clear all groups and their members.
     */
    void clearAllGroups();

    /**
     * Delete temporary test users (email pattern: temp_user@*).
     */
    void clearTempUsers();
}
