package kr.co.jparangdev.boardbuddy.api.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kr.co.jparangdev.boardbuddy.api.user.dto.UserDto;
import kr.co.jparangdev.boardbuddy.application.user.usecase.UserQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserQueryUseCase userQueryUseCase;
    private final kr.co.jparangdev.boardbuddy.application.user.usecase.UserCommandUseCase userCommandUseCase;
    private final UserDtoMapper mapper;

    /**
     * Get current authenticated user
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto.Response> getCurrentUser() {
        User user = userQueryUseCase.getCurrentUser();
        return ResponseEntity.ok(mapper.toResponse(user));
    }

    /**
     * Search users by nickname keyword
     */
    @GetMapping("/search")
    public ResponseEntity<UserDto.SearchResponse> searchUsers(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(mapper.toSearchResponse(userQueryUseCase.searchUsers(keyword)));
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto.Response> getUserById(@PathVariable("id") Long id) {
        return userQueryUseCase.getUserById(id)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Delete current user account
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser() {
        User user = userQueryUseCase.getCurrentUser();
        userCommandUseCase.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }
}
