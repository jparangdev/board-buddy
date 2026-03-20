package kr.co.jparangdev.boardbuddy.application.group.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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

import kr.co.jparangdev.boardbuddy.domain.group.Group;
import kr.co.jparangdev.boardbuddy.domain.group.GroupMember;
import kr.co.jparangdev.boardbuddy.domain.group.exception.NotGroupOwnerException;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupMemberRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.exception.UserNotGroupMemberException;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class GroupManagementServiceTest {

    private GroupManagementService groupManagementService;

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GroupMemberRepository groupMemberRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        groupManagementService = new GroupManagementService(groupRepository, groupMemberRepository, userRepository);
    }

    private void mockSecurityContext(Long userId) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(userId);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Create Group Success")
    void createGroupSuccess() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            // given
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(1L);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Group group = Group.builder().id(1L).name("Test Group").ownerId(1L).build();
            given(groupRepository.save(any(Group.class))).willReturn(group);

            // when
            Group result = groupManagementService.createGroup("Test Group", List.of(2L));

            // then
            assertThat(result.getId()).isEqualTo(1L);
            verify(groupRepository).save(any(Group.class));
            // owner(1L) + member(2L) = 2 saves
            verify(groupMemberRepository, org.mockito.Mockito.times(2)).save(any(GroupMember.class));
        }
    }

    @Test
    @DisplayName("Get Group Members Success")
    void getGroupMembersSuccess() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            // given
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(1L);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Group group = Group.builder().id(1L).build();
            given(groupRepository.findById(1L)).willReturn(Optional.of(group));
            given(groupMemberRepository.existsByGroupIdAndUserId(1L, 1L)).willReturn(true);

            GroupMember member = GroupMember.builder().userId(1L).build();
            given(groupMemberRepository.findAllByGroupId(1L)).willReturn(List.of(member));

            User user = User.builder().id(1L).nickname("tester").build();
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            // when
            List<User> result = groupManagementService.getGroupMembers(1L);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNickname()).isEqualTo("tester");
        }
    }

    @Test
    @DisplayName("Get Group Members Failed - Not Member")
    void getGroupMembersNotMember() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            // given
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(1L);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Group group = Group.builder().id(1L).build();
            given(groupRepository.findById(1L)).willReturn(Optional.of(group));
            given(groupMemberRepository.existsByGroupIdAndUserId(1L, 1L)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> groupManagementService.getGroupMembers(1L))
                .isInstanceOf(UserNotGroupMemberException.class);
        }
    }

    @Test
    @DisplayName("Get Group Detail Success")
    void getGroupDetailSuccess() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            // given
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(1L);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Group group = Group.builder().id(1L).name("Test Group").build();
            given(groupRepository.findById(1L)).willReturn(Optional.of(group));
            given(groupMemberRepository.existsByGroupIdAndUserId(1L, 1L)).willReturn(true);

            // when
            Group result = groupManagementService.getGroupDetail(1L);

            // then
            assertThat(result.getName()).isEqualTo("Test Group");
        }
    }

    @Test
    @DisplayName("Get My Groups Success")
    void getMyGroupsSuccess() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            // given
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(1L);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            GroupMember membership = GroupMember.builder().groupId(1L).build();
            given(groupMemberRepository.findAllByUserIdOrderByDisplayOrderAsc(1L)).willReturn(List.of(membership));

            Group group = Group.builder().id(1L).name("Test Group").build();
            given(groupRepository.findAllByIds(List.of(1L))).willReturn(List.of(group));

            // when
            List<Group> result = groupManagementService.getMyGroups();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Test Group");
        }
    }

    @Test
    @DisplayName("Delete Group Success")
    void deleteGroupSuccess() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            // given
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(1L);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Group group = Group.builder().id(1L).ownerId(1L).build();
            given(groupRepository.findById(1L)).willReturn(Optional.of(group));

            // when
            groupManagementService.deleteGroup(1L);

            // then
            verify(groupMemberRepository).deleteAllByGroupId(1L);
            verify(groupRepository).deleteById(1L);
        }
    }

    @Test
    @DisplayName("Delete Group Failed - Not Owner")
    void deleteGroupNotOwner() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            // given
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(2L); // Not owner
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Group group = Group.builder().id(1L).ownerId(1L).build();
            given(groupRepository.findById(1L)).willReturn(Optional.of(group));

            // when & then
            assertThatThrownBy(() -> groupManagementService.deleteGroup(1L))
                .isInstanceOf(NotGroupOwnerException.class);
        }
    }
}
