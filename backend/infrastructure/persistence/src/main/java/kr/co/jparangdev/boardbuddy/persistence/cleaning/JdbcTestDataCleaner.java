package kr.co.jparangdev.boardbuddy.persistence.cleaning;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.cleaning.port.TestDataCleaner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Infrastructure adapter for cleaning test data.
 * Only active in local and dev profiles.
 * Uses JDBC for efficient bulk deletion.
 */
@Slf4j
@Component
@Profile({"local", "dev"})
@RequiredArgsConstructor
public class JdbcTestDataCleaner implements TestDataCleaner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void resetAll() {
        log.info("Resetting all test data...");
        clearAllGameSessions();
        clearAllGroups();
        clearTempUsers();
        log.info("Test data reset completed");
    }

    @Override
    @Transactional
    public void clearAllGameSessions() {
        log.info("Clearing all game sessions...");

        // Delete game results first (foreign key constraint)
        int resultsDeleted = jdbcTemplate.update("DELETE FROM game_results");
        log.debug("Deleted {} game results", resultsDeleted);

        // Delete game sessions
        int sessionsDeleted = jdbcTemplate.update("DELETE FROM game_sessions");
        log.info("Cleared {} game sessions", sessionsDeleted);
    }

    @Override
    @Transactional
    public void clearAllGroups() {
        log.info("Clearing all groups...");

        // Delete group members first (foreign key constraint)
        int membersDeleted = jdbcTemplate.update("DELETE FROM group_members");
        log.debug("Deleted {} group members", membersDeleted);

        // Delete groups
        int groupsDeleted = jdbcTemplate.update("DELETE FROM groups");
        log.info("Cleared {} groups", groupsDeleted);
    }

    @Override
    @Transactional
    public void clearTempUsers() {
        log.info("Clearing temporary test users...");

        // Delete users with email pattern 'temp_user@%'
        int usersDeleted = jdbcTemplate.update(
            "DELETE FROM users WHERE email LIKE ?",
            "temp_user@%"
        );

        log.info("Cleared {} temporary users", usersDeleted);
    }
}
