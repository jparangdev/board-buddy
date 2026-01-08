package kr.co.jparangdev.boardbuddy.api.user;

import kr.co.jparangdev.boardbuddy.api.user.dto.UserDto;
import kr.co.jparangdev.boardbuddy.application.user.UserManagementUseCase;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserManagementUseCase userManagementUseCase;
    private final UserDtoMapper mapper;

    /**
     * Get current authenticated user
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto.Response> getCurrentUser() {
        User user = userManagementUseCase.getCurrentUser();
        return ResponseEntity.ok(mapper.toResponse(user));
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto.Response> getUserById(@PathVariable Long id) {
        return userManagementUseCase.getUserById(id)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
