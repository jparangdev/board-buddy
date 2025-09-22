package kr.co.jparangdev.boardbuddies.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddies.application.usecases.UserManagementUseCase;
import kr.co.jparangdev.boardbuddies.web.dto.UserDto;
import kr.co.jparangdev.boardbuddies.web.mapper.UserDtoMapper;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for User operations
 */
@RestController
@RequestMapping(value = "/api/users", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

	private final UserManagementUseCase userManagementUseCase;
	private final UserDtoMapper userDtoMapper;

	/**
	 * Create a new user
	 * @param createRequest the user creation request
	 * @return the created user
	 */
 @PostMapping(consumes = "application/json")
 public ResponseEntity<UserDto.Response> createUser(@Valid @RequestBody UserDto.CreateRequest createRequest) {
		var user = userDtoMapper.toDomain(createRequest);
		var createdUser = userManagementUseCase.createUser(user);
		return new ResponseEntity<>(userDtoMapper.toResponse(createdUser), HttpStatus.CREATED);
	}

	/**
	 * Get a user by ID
	 * @param id the user ID
	 * @return the user if found
	 */
	@GetMapping("/{id}")
	public ResponseEntity<UserDto.Response> getUserById(@PathVariable Long id) {
		return userManagementUseCase.getUserById(id)
			.map(user -> ResponseEntity.ok(userDtoMapper.toResponse(user)))
			.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Get all users
	 * @return list of all users
	 */
	@GetMapping
	public ResponseEntity<List<UserDto.Response>> getAllUsers() {
		var users = userManagementUseCase.getAllUsers();
		var userResponses = users.stream()
			.map(userDtoMapper::toResponse)
			.toList();
		return ResponseEntity.ok(userResponses);
	}

	/**
	 * Update a user
	 * @param id the user ID
	 * @param updateRequest the user update request
	 * @return the updated user
	 */
 @PutMapping(value = "/{id}", consumes = "application/json")
 public ResponseEntity<UserDto.Response> updateUser(
 	@PathVariable Long id,
 	@Valid @RequestBody UserDto.UpdateRequest updateRequest) {
		var user = userDtoMapper.toDomain(updateRequest);
		var updatedUser = userManagementUseCase.updateUser(id, user);
		return ResponseEntity.ok(userDtoMapper.toResponse(updatedUser));
	}

	/**
	 * Delete a user
	 * @param id the user ID
	 * @return no content response
	 */
 @DeleteMapping("/{id}")
 public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
 	userManagementUseCase.deleteUser(id);
 	return ResponseEntity.noContent().build();
 }
 	/**
	 * Search a user by username or email
	 * Exactly one of username or email must be provided
	 */
	@GetMapping(value = "/search")
	public ResponseEntity<UserDto.Response> searchUser(
		@RequestParam(required = false) String username,
		@RequestParam(required = false) String email) {
		boolean hasUsername = username != null && !username.isBlank();
		boolean hasEmail = email != null && !email.isBlank();
		if (hasUsername == hasEmail) { // both true or both false
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		if (hasUsername) {
			return userManagementUseCase.findByUsername(username)
				.map(user -> ResponseEntity.ok(userDtoMapper.toResponse(user)))
				.orElse(ResponseEntity.notFound().build());
		}
		return userManagementUseCase.findByEmail(email)
			.map(user -> ResponseEntity.ok(userDtoMapper.toResponse(user)))
			.orElse(ResponseEntity.notFound().build());
	}
}
