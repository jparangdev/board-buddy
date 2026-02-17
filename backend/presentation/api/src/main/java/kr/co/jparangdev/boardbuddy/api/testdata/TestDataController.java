package kr.co.jparangdev.boardbuddy.api.testdata;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kr.co.jparangdev.boardbuddy.application.cleaning.port.TestDataCleaner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Test data management controller for development environment.
 * Only available in local and dev profiles.
 * Provides endpoints to reset test data for regression testing.
 *
 * This controller has NO business logic - it only delegates to the port.
 */
@Slf4j
@RestController
@RequestMapping("/api/test-data")
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class TestDataController {

    private final TestDataCleaner testDataCleaner;

    /**
     * Reset all test data (sessions, groups, temp users)
     */
    @PostMapping("/reset")
    public ResponseEntity<TestDataResponse> resetAllTestData() {
        log.info("Resetting all test data via API");
        testDataCleaner.resetAll();
        return ResponseEntity.ok(new TestDataResponse("All test data has been reset"));
    }

    /**
     * Clear all game sessions only
     */
    @DeleteMapping("/sessions")
    public ResponseEntity<TestDataResponse> clearGameSessions() {
        log.info("Clearing game sessions via API");
        testDataCleaner.clearAllGameSessions();
        return ResponseEntity.ok(new TestDataResponse("All game sessions have been cleared"));
    }

    /**
     * Clear all groups only
     */
    @DeleteMapping("/groups")
    public ResponseEntity<TestDataResponse> clearGroups() {
        log.info("Clearing groups via API");
        testDataCleaner.clearAllGroups();
        return ResponseEntity.ok(new TestDataResponse("All groups have been cleared"));
    }

    /**
     * Delete temporary test users (email pattern: temp_user@*)
     */
    @DeleteMapping("/temp-users")
    public ResponseEntity<TestDataResponse> clearTempUsers() {
        log.info("Clearing temporary users via API");
        testDataCleaner.clearTempUsers();
        return ResponseEntity.ok(new TestDataResponse("Temporary users have been cleared"));
    }

    /**
     * Response DTO for test data operations
     */
    public record TestDataResponse(String message) {}
}
