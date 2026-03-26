package kr.co.jparangdev.boardbuddy.application.invitation.service;

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

import kr.co.jparangdev.boardbuddy.application.invitation.dto.InvitationInfo;
import kr.co.jparangdev.boardbuddy.domain.group.Group;
import kr.co.jparangdev.boardbuddy.domain.group.GroupMember;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupMemberRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupRepository;
import kr.co.jparangdev.boardbuddy.domain.invitation.Invitation;
import kr.co.jparangdev.boardbuddy.domain.invitation.InvitationStatus;
import kr.co.jparangdev.boardbuddy.domain.invitation.exception.DuplicateInvitationException;
import kr.co.jparangdev.boardbuddy.domain.invitation.exception.InvitationAccessDeniedException;
import kr.co.jparangdev.boardbuddy.domain.invitation.exception.InvitationNotPendingException;
import kr.co.jparangdev.boardbuddy.domain.invitation.repository.InvitationRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.exception.UserNotGroupMemberException;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class InvitationManagementServiceTest {

    private InvitationManagementService service;

    @Mock
    private InvitationRepository invitationRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GroupMemberRepository groupMemberRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        service = new InvitationManagementService(invitationRepository, groupRepository, groupMemberRepository, userRepository);
    }

    private void mockSecurityContext(MockedStatic<SecurityContextHolder> holder, Long userId) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(userId);
        holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @Test
    @DisplayName("Invite user successfully")
    void inviteUser_success() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContext(holder, 1L);

            Group group = Group.builder().id(10L).name("Test Group").ownerId(1L).build();
            given(groupRepository.findById(10L)).willReturn(Optional.of(group));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(true);
            given(userRepository.existsById(2L)).willReturn(true);
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 2L)).willReturn(false);
            given(invitationRepository.existsByGroupIdAndInviteeIdAndStatus(10L, 2L, InvitationStatus.PENDING)).willReturn(false);
            given(invitationRepository.save(any())).willReturn(Invitation.create(10L, 1L, 2L));

            service.inviteUser(10L, 2L);

            verify(invitationRepository).save(any(Invitation.class));
        }
    }

    @Test
    @DisplayName("Invite user fails when current user is not a group member")
    void inviteUser_fails_when_not_member() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContext(holder, 1L);

            Group group = Group.builder().id(10L).name("Test Group").ownerId(99L).build();
            given(groupRepository.findById(10L)).willReturn(Optional.of(group));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(false);

            assertThatThrownBy(() -> service.inviteUser(10L, 2L))
                    .isInstanceOf(UserNotGroupMemberException.class);
        }
    }

    @Test
    @DisplayName("Invite user fails when pending invitation already exists")
    void inviteUser_fails_when_duplicate_invitation() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContext(holder, 1L);

            Group group = Group.builder().id(10L).name("Test Group").ownerId(1L).build();
            given(groupRepository.findById(10L)).willReturn(Optional.of(group));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(true);
            given(userRepository.existsById(2L)).willReturn(true);
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 2L)).willReturn(false);
            given(invitationRepository.existsByGroupIdAndInviteeIdAndStatus(10L, 2L, InvitationStatus.PENDING)).willReturn(true);

            assertThatThrownBy(() -> service.inviteUser(10L, 2L))
                    .isInstanceOf(DuplicateInvitationException.class);
        }
    }

    @Test
    @DisplayName("Accept invitation adds user as group member")
    void respondToInvitation_accept_adds_member() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContext(holder, 2L);

            Invitation pending = Invitation.builder()
                    .id(1L).groupId(10L).inviterId(1L).inviteeId(2L)
                    .status(InvitationStatus.PENDING).build();
            given(invitationRepository.findById(1L)).willReturn(Optional.of(pending));
            given(groupMemberRepository.save(any())).willReturn(GroupMember.create(10L, 2L));
            given(invitationRepository.save(any())).willReturn(pending.accept());

            service.respondToInvitation(1L, true);

            verify(groupMemberRepository).save(any(GroupMember.class));
            verify(invitationRepository).save(any(Invitation.class));
        }
    }

    @Test
    @DisplayName("Reject invitation does not add user as group member")
    void respondToInvitation_reject_skips_member_save() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContext(holder, 2L);

            Invitation pending = Invitation.builder()
                    .id(1L).groupId(10L).inviterId(1L).inviteeId(2L)
                    .status(InvitationStatus.PENDING).build();
            given(invitationRepository.findById(1L)).willReturn(Optional.of(pending));
            given(invitationRepository.save(any())).willReturn(pending.reject());

            service.respondToInvitation(1L, false);

            verify(groupMemberRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Respond fails when user is not the invitee")
    void respondToInvitation_fails_when_not_invitee() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContext(holder, 99L);

            Invitation pending = Invitation.builder()
                    .id(1L).groupId(10L).inviterId(1L).inviteeId(2L)
                    .status(InvitationStatus.PENDING).build();
            given(invitationRepository.findById(1L)).willReturn(Optional.of(pending));

            assertThatThrownBy(() -> service.respondToInvitation(1L, true))
                    .isInstanceOf(InvitationAccessDeniedException.class);
        }
    }

    @Test
    @DisplayName("Respond fails when invitation is already responded")
    void respondToInvitation_fails_when_not_pending() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContext(holder, 2L);

            Invitation accepted = Invitation.builder()
                    .id(1L).groupId(10L).inviterId(1L).inviteeId(2L)
                    .status(InvitationStatus.ACCEPTED).build();
            given(invitationRepository.findById(1L)).willReturn(Optional.of(accepted));

            assertThatThrownBy(() -> service.respondToInvitation(1L, true))
                    .isInstanceOf(InvitationNotPendingException.class);
        }
    }

    @Test
    @DisplayName("Get pending invitations returns enriched info")
    void getMyPendingInvitations_returns_list() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContext(holder, 2L);

            Invitation pending = Invitation.builder()
                    .id(1L).groupId(10L).inviterId(1L).inviteeId(2L)
                    .status(InvitationStatus.PENDING).build();
            given(invitationRepository.findAllByInviteeIdAndStatus(2L, InvitationStatus.PENDING))
                    .willReturn(List.of(pending));
            given(groupRepository.findById(10L))
                    .willReturn(Optional.of(Group.builder().id(10L).name("Test Group").ownerId(1L).build()));
            given(userRepository.findById(1L))
                    .willReturn(Optional.of(User.builder().id(1L).nickname("Alice").build()));

            List<InvitationInfo> result = service.getMyPendingInvitations();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).groupName()).isEqualTo("Test Group");
            assertThat(result.get(0).inviterNickname()).isEqualTo("Alice");
        }
    }
}
