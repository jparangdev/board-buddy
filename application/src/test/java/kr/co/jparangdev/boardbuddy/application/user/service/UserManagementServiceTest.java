package kr.co.jparangdev.boardbuddy.application.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import kr.co.jparangdev.boardbuddy.application.user.exception.UserNotFoundException;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    private UserManagementService userManagementService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userManagementService = new UserManagementService(userRepository);
    }

    @Test
    @DisplayName("Get User By ID Success")
    void getUserByIdSuccess() {
        // given
        User user = User.builder().id(1L).nickname("test").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        Optional<User> result = userManagementService.getUserById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Get Current User Success")
    void getCurrentUserSuccess() {
        // given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(1L);
        
        User user = User.builder().id(1L).nickname("test").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // when
            User result = userManagementService.getCurrentUser();

            // then
            assertThat(result.getId()).isEqualTo(1L);
        }
    }

    @Test
    @DisplayName("Get Current User Failed - Missing Authentication")
    void getCurrentUserMissingAuth() {
        // given
        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(null);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // when & then
            assertThatThrownBy(() -> userManagementService.getCurrentUser())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Authentication is missing");
        }
    }

    @Test
    @DisplayName("Get Current User Failed - User Not Found")
    void getCurrentUserNotFound() {
        // given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(1L);
        
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // when & then
            assertThatThrownBy(() -> userManagementService.getCurrentUser())
                .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Search Users Success")
    void searchUsersSuccess() {
        // given
        User user = User.builder().id(1L).nickname("tester").build();
        given(userRepository.searchByNicknameContaining(anyString(), anyInt())).willReturn(List.of(user));

        // when
        List<User> result = userManagementService.searchUsers("test");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("Search Users Empty Keyword")
    void searchUsersEmpty() {
        // when
        List<User> result = userManagementService.searchUsers("");

        // then
        assertThat(result).isEmpty();
    }
}
