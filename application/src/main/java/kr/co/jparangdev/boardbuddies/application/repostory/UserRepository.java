package kr.co.jparangdev.boardbuddies.application.repostory;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddies.domain.entity.User;

/**
 * Repository interface for User entity (Secondary Port)
 * This is a secondary port (driven port) in hexagonal architecture that will be implemented
 * by an adapter in the infrastructure layer
 */
public interface UserRepository {

    /**
     * Save a user
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);

    /**
     * Find a user by ID
     * @param id the user ID
     * @return the user if found
     */
    Optional<User> findById(Long id);

    /**
     * Find all users
     * @return list of all users
     */
    List<User> findAll();

    /**
     * Delete a user
     * @param id the user ID to delete
     */
    void deleteById(Long id);

    /**
     * Find a user by username
     * @param username the username to search for
     * @return the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by email
     * @param email the email to search for
     * @return the user if found
     */
    Optional<User> findByEmail(String email);
}
