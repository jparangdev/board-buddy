package kr.co.jparangdev.boardbuddies.application.usecases;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddies.domain.entity.User;

public interface UserManagementUseCase {

	/**
	 * Create a new user
	 * @param user the user to create
	 * @return the created user
	 */
	User createUser(User user);

	/**
	 * Get a user by ID
	 * @param id the user ID
	 * @return the user if found
	 */
	Optional<User> getUserById(Long id);

	/**
	 * Get all users
	 * @return list of all users
	 */
	List<User> getAllUsers();

	/**
	 * Update a user
	 * @param id the user ID
	 * @param user the updated user data
	 * @return the updated user
	 */
	User updateUser(Long id, User user);

	/**
	 * Delete a user
	 * @param id the user ID
	 */
	void deleteUser(Long id);

	/**
	 * Find a user by username
	 * @param username the username
	 * @return the user if found
	 */
	Optional<User> findByUsername(String username);

	/**
	 * Find a user by email
	 * @param email the email
	 * @return the user if found
	 */
	Optional<User> findByEmail(String email);
}
