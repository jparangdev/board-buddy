package kr.co.jparangdev.boardbuddy.application.game.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

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

import kr.co.jparangdev.boardbuddy.domain.game.CustomGame;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import kr.co.jparangdev.boardbuddy.domain.game.exception.CustomGameNotFoundException;
import kr.co.jparangdev.boardbuddy.domain.game.exception.DuplicateCustomGameNameException;
import kr.co.jparangdev.boardbuddy.domain.game.repository.CustomGameRepository;
import kr.co.jparangdev.boardbuddy.domain.group.Group;
import kr.co.jparangdev.boardbuddy.domain.group.exception.GroupNotFoundException;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupMemberRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupRepository;
import kr.co.jparangdev.boardbuddy.domain.user.exception.UserNotGroupMemberException;

@ExtendWith(MockitoExtension.class)
class CustomGameManagementServiceTest {

    private CustomGameManagementService customGameManagementService;

    @Mock private CustomGameRepository customGameRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private GroupMemberRepository groupMemberRepository;

    @BeforeEach
    void setUp() {
        customGameManagementService = new CustomGameManagementService(
            customGameRepository, groupRepository, groupMemberRepository);
    }

    private void mockSecurityContext(MockedStatic<SecurityContextHolder> holder, Long userId) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(userId);
        holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @Test
    @DisplayName("Get Custom Games Success")
    void getCustomGamesByGroupSuccess() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.of(Group.builder().id(10L).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(true);
            CustomGame game = CustomGame.builder().id(1L).groupId(10L).name("House Chess").build();
            given(customGameRepository.findAllByGroupId(10L)).willReturn(List.of(game));

            // when
            List<CustomGame> result = customGameManagementService.getCustomGamesByGroup(10L);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("House Chess");
        }
    }

    @Test
    @DisplayName("Get Custom Games Failed - Group Not Found")
    void getCustomGamesByGroupNotFound() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> customGameManagementService.getCustomGamesByGroup(10L))
                .isInstanceOf(GroupNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Get Custom Games Failed - Not Member")
    void getCustomGamesByGroupNotMember() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.of(Group.builder().id(10L).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> customGameManagementService.getCustomGamesByGroup(10L))
                .isInstanceOf(UserNotGroupMemberException.class);
        }
    }

    @Test
    @DisplayName("Get Custom Game Detail Success")
    void getCustomGameDetailSuccess() {
        // given
        CustomGame game = CustomGame.builder().id(5L).name("House Chess").build();
        given(customGameRepository.findById(5L)).willReturn(Optional.of(game));

        // when
        CustomGame result = customGameManagementService.getCustomGameDetail(5L);

        // then
        assertThat(result.getName()).isEqualTo("House Chess");
    }

    @Test
    @DisplayName("Get Custom Game Detail Failed - Not Found")
    void getCustomGameDetailNotFound() {
        // given
        given(customGameRepository.findById(5L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customGameManagementService.getCustomGameDetail(5L))
            .isInstanceOf(CustomGameNotFoundException.class);
    }

    @Test
    @DisplayName("Create Custom Game Success")
    void createCustomGameSuccess() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.of(Group.builder().id(10L).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(true);
            given(customGameRepository.existsByGroupIdAndName(10L, "House Chess")).willReturn(false);
            CustomGame saved = CustomGame.builder().id(1L).groupId(10L).name("House Chess").build();
            given(customGameRepository.save(any(CustomGame.class))).willReturn(saved);

            // when
            CustomGame result = customGameManagementService.createCustomGame(10L, "House Chess", 2, 4, ScoreStrategy.RANK_ONLY);

            // then
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("House Chess");
        }
    }

    @Test
    @DisplayName("Create Custom Game Failed - Duplicate Name")
    void createCustomGameDuplicateName() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.of(Group.builder().id(10L).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(true);
            given(customGameRepository.existsByGroupIdAndName(10L, "House Chess")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> customGameManagementService.createCustomGame(10L, "House Chess", 2, 4, ScoreStrategy.RANK_ONLY))
                .isInstanceOf(DuplicateCustomGameNameException.class);
        }
    }

    @Test
    @DisplayName("Create Custom Game Failed - Not Member")
    void createCustomGameNotMember() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.of(Group.builder().id(10L).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> customGameManagementService.createCustomGame(10L, "House Chess", 2, 4, ScoreStrategy.RANK_ONLY))
                .isInstanceOf(UserNotGroupMemberException.class);
        }
    }

    @Test
    @DisplayName("Create Custom Game Failed - Group Not Found")
    void createCustomGameGroupNotFound() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> customGameManagementService.createCustomGame(10L, "House Chess", 2, 4, ScoreStrategy.RANK_ONLY))
                .isInstanceOf(GroupNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Get Custom Games Failed - Authentication Missing")
    void getCustomGamesByGroupAuthenticationMissing() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(null);
            holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // when & then
            assertThatThrownBy(() -> customGameManagementService.getCustomGamesByGroup(10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Authentication is missing");
        }
    }

    @Test
    @DisplayName("Create Custom Game Failed - Authentication Missing")
    void createCustomGameAuthenticationMissing() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(null);
            holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // when & then
            assertThatThrownBy(() -> customGameManagementService.createCustomGame(10L, "House Chess", 2, 4, ScoreStrategy.RANK_ONLY))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Authentication is missing");
        }
    }
}
