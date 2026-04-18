package kr.co.jparangdev.boardbuddy.application.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.exception.UserNotFoundException;
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
    @DisplayName("Search Users Success - Exact UserTag Match")
    void searchUsersSuccess() {
        // given
        User user = User.builder().id(1L).nickname("tester").discriminator("AB12").build();
        given(userRepository.findByNicknameAndDiscriminator("tester", "AB12")).willReturn(Optional.of(user));

        // when
        List<User> result = userManagementService.searchUsers("tester#AB12");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("Search Users - No Hash Returns Empty")
    void searchUsersNoHash() {
        // when
        List<User> result = userManagementService.searchUsers("tester");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Search Users - Empty Keyword Returns Empty")
    void searchUsersEmpty() {
        // when
        List<User> result = userManagementService.searchUsers("");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Search Users - User Not Found Returns Empty")
    void searchUsersNotFound() {
        // given
        given(userRepository.findByNicknameAndDiscriminator("ghost", "ZZ99")).willReturn(Optional.empty());

        // when
        List<User> result = userManagementService.searchUsers("ghost#ZZ99");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Search Users - Email search finds user by full email")
    void searchUsersByEmail() {
        // given
        User user = User.builder().id(1L).nickname("tester").email("tester@example.com").build();
        given(userRepository.findByEmail("tester@example.com")).willReturn(Optional.of(user));

        // when
        List<User> result = userManagementService.searchUsers("tester@example.com");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("Search Users - Email not found returns empty")
    void searchUsersByEmailNotFound() {
        // given
        given(userRepository.findByEmail("unknown@example.com")).willReturn(Optional.empty());

        // when
        List<User> result = userManagementService.searchUsers("unknown@example.com");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Update Nickname - keeps discriminator when nickname is unchanged")
    void updateNicknameUnchanged() {
        // given
        User user = User.builder().id(1L).nickname("tester").discriminator("AB12").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        userManagementService.updateNickname(1L, "tester");

        // then
        assertThat(user.getNickname()).isEqualTo("tester");
        assertThat(user.getDiscriminator()).isEqualTo("AB12");
        verify(userRepository, never()).generateUniqueDiscriminator("tester");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Update Nickname - generates new discriminator when current one is taken")
    void updateNicknameGeneratesNewDiscriminatorWhenTaken() {
        // given
        User user = User.builder().id(1L).nickname("tester").discriminator("AB12").build();
        User otherUser = User.builder().id(2L).nickname("alice").discriminator("AB12").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.findByNicknameAndDiscriminator("alice", "AB12")).willReturn(Optional.of(otherUser));
        given(userRepository.generateUniqueDiscriminator("alice")).willReturn("CD34");

        // when
        userManagementService.updateNickname(1L, "alice");

        // then
        assertThat(user.getNickname()).isEqualTo("alice");
        assertThat(user.getDiscriminator()).isEqualTo("CD34");
        verify(userRepository).generateUniqueDiscriminator("alice");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Update Nickname - reuses discriminator when available")
    void updateNicknameReusesDiscriminatorWhenAvailable() {
        // given
        User user = User.builder().id(1L).nickname("tester").discriminator("AB12").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.findByNicknameAndDiscriminator("alice", "AB12")).willReturn(Optional.empty());

        // when
        userManagementService.updateNickname(1L, "alice");

        // then
        assertThat(user.getNickname()).isEqualTo("alice");
        assertThat(user.getDiscriminator()).isEqualTo("AB12");
        verify(userRepository, never()).generateUniqueDiscriminator("alice");
        verify(userRepository).save(user);
    }
}
