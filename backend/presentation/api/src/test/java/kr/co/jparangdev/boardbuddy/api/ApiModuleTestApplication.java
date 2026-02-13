package kr.co.jparangdev.boardbuddy.api;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Dummy application class for testing purposes.
 * <p>
 * The `presentation/api` module is a library module, not an independently executable application.
 * Therefore, `src/main` does not contain a main class annotated with `@SpringBootApplication`.
 * <p>
 * Slice tests like `@WebMvcTest` look for `@SpringBootConfiguration` at runtime.
 * If no main class exists, an "Unable to find a @SpringBootConfiguration" error occurs.
 * <p>
 * This class serves as a configuration entry point for the Spring context during tests.
 */
@SpringBootApplication
public class ApiModuleTestApplication {
    public void main(String[] args) {
        // Test application entry point
    }
}
