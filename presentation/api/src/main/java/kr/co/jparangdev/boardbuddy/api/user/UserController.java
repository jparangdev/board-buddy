package kr.co.jparangdev.boardbuddy.api.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kr.co.jparangdev.boardbuddy.api.user.dto.UserDto;
import kr.co.jparangdev.boardbuddy.application.user.usecase.*;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final GetUserByIdUseCase getUserByIdUseCase;
    private final SearchUsersUseCase searchUsersUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UserDtoMapper mapper;

    /**
     * Get current authenticated user
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto.Response> getCurrentUser() {
        User user = getCurrentUserUseCase.getCurrentUser();
        return ResponseEntity.ok(mapper.toResponse(user));
    }

    /**
     * Search users by nickname keyword
     */
    @GetMapping("/search")
    public ResponseEntity<UserDto.SearchResponse> searchUsers(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(mapper.toSearchResponse(searchUsersUseCase.searchUsers(keyword)));
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto.Response> getUserById(@PathVariable("id") Long id) {
        return getUserByIdUseCase.getUserById(id)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
