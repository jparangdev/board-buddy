package kr.co.jparangdev.boardbuddies.web.adapters.input.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddies.application.usecases.UserManagementUseCase;
import kr.co.jparangdev.boardbuddies.web.adapters.input.rest.dto.UserDto;
import kr.co.jparangdev.boardbuddies.web.adapters.input.rest.mapper.UserDtoMapper;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for User operations
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserManagementUseCase userManagementUseCase;
	private final UserDtoMapper userDtoMapper;

	/**
	 * Create a new user
	 * @param createRequest the user creation request
	 * @return the created user
	 */
	@PostMapping
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
	@PutMapping("/{id}")
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
}
