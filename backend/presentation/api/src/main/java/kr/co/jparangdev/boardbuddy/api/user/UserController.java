package kr.co.jparangdev.boardbuddy.api.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddy.api.user.dto.UserDto;
import kr.co.jparangdev.boardbuddy.application.auth.dto.SocialAccountDto;
import kr.co.jparangdev.boardbuddy.application.auth.usecase.SocialAccountUseCase;
import kr.co.jparangdev.boardbuddy.application.user.usecase.UserQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserQueryUseCase userQueryUseCase;
    private final kr.co.jparangdev.boardbuddy.application.user.usecase.UserCommandUseCase userCommandUseCase;
    private final SocialAccountUseCase socialAccountUseCase;
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
     * Update current user's nickname
     */
    @PatchMapping("/me/nickname")
    public ResponseEntity<UserDto.Response> updateNickname(@Valid @RequestBody UserDto.UpdateNicknameRequest request) {
        User user = userQueryUseCase.getCurrentUser();
        userCommandUseCase.updateNickname(user.getId(), request.getNickname().trim());
        User updated = userQueryUseCase.getUserById(user.getId())
            .orElseThrow(() -> new IllegalStateException("User not found after update"));
        return ResponseEntity.ok(mapper.toResponse(updated));
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

    /**
     * Get linked OAuth accounts for current user
     */
    @GetMapping("/me/social-accounts")
    public ResponseEntity<UserDto.SocialAccountListResponse> getLinkedAccounts() {
        User user = userQueryUseCase.getCurrentUser();
        List<SocialAccountDto> accounts = socialAccountUseCase.getLinkedAccounts(user.getId());
        List<UserDto.SocialAccountResponse> responses = accounts.stream()
                .map(dto -> UserDto.SocialAccountResponse.builder()
                        .provider(dto.provider())
                        .linkedAt(dto.linkedAt())
                        .build())
                .toList();
        return ResponseEntity.ok(UserDto.SocialAccountListResponse.builder().accounts(responses).build());
    }

    /**
     * Link an OAuth provider account to the current user
     */
    @PostMapping("/me/social-accounts/{provider}/link")
    public ResponseEntity<Void> linkAccount(
            @PathVariable("provider") String provider,
            @Valid @RequestBody UserDto.LinkAccountRequest request) {
        User user = userQueryUseCase.getCurrentUser();
        socialAccountUseCase.linkAccount(user.getId(), provider, request.getCode(), request.getRedirectUri());
        return ResponseEntity.noContent().build();
    }

    /**
     * Unlink an OAuth provider account from the current user
     */
    @DeleteMapping("/me/social-accounts/{provider}")
    public ResponseEntity<Void> unlinkAccount(@PathVariable("provider") String provider) {
        User user = userQueryUseCase.getCurrentUser();
        socialAccountUseCase.unlinkAccount(user.getId(), provider);
        return ResponseEntity.noContent().build();
    }
}
