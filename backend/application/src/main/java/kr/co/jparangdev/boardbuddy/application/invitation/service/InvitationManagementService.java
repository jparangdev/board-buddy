package kr.co.jparangdev.boardbuddy.application.invitation.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.invitation.dto.InvitationInfo;
import kr.co.jparangdev.boardbuddy.application.invitation.usecase.InvitationCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.invitation.usecase.InvitationQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.group.GroupMember;
import kr.co.jparangdev.boardbuddy.domain.group.exception.GroupNotFoundException;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupMemberRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupRepository;
import kr.co.jparangdev.boardbuddy.domain.invitation.Invitation;
import kr.co.jparangdev.boardbuddy.domain.invitation.InvitationStatus;
import kr.co.jparangdev.boardbuddy.domain.invitation.exception.DuplicateInvitationException;
import kr.co.jparangdev.boardbuddy.domain.invitation.exception.InvitationAccessDeniedException;
import kr.co.jparangdev.boardbuddy.domain.invitation.exception.InvitationNotFoundException;
import kr.co.jparangdev.boardbuddy.domain.invitation.exception.InvitationNotPendingException;
import kr.co.jparangdev.boardbuddy.domain.invitation.repository.InvitationRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.exception.UserNotFoundException;
import kr.co.jparangdev.boardbuddy.domain.user.exception.UserNotGroupMemberException;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationManagementService implements InvitationCommandUseCase, InvitationQueryUseCase {

    private final InvitationRepository invitationRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void inviteUser(Long groupId, Long inviteeId) {
        Long currentUserId = getCurrentUserId();

        groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new UserNotGroupMemberException(groupId, currentUserId);
        }

        if (!userRepository.existsById(inviteeId)) {
            throw new UserNotFoundException(inviteeId);
        }

        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, inviteeId)) {
            throw new DuplicateInvitationException(groupId, inviteeId);
        }

        if (invitationRepository.existsByGroupIdAndInviteeIdAndStatus(groupId, inviteeId, InvitationStatus.PENDING)) {
            throw new DuplicateInvitationException(groupId, inviteeId);
        }

        invitationRepository.save(Invitation.create(groupId, currentUserId, inviteeId));
    }

    @Override
    @Transactional
    public void respondToInvitation(Long invitationId, boolean accept) {
        Long currentUserId = getCurrentUserId();

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException(invitationId));

        if (!invitation.getInviteeId().equals(currentUserId)) {
            throw new InvitationAccessDeniedException(invitationId, currentUserId);
        }

        if (!invitation.isPending()) {
            throw new InvitationNotPendingException(invitationId);
        }

        if (accept) {
            groupMemberRepository.save(GroupMember.create(invitation.getGroupId(), invitation.getInviteeId()));
            invitationRepository.save(invitation.accept());
        } else {
            invitationRepository.save(invitation.reject());
        }
    }

    @Override
    public List<InvitationInfo> getMyPendingInvitations() {
        Long currentUserId = getCurrentUserId();

        return invitationRepository.findAllByInviteeIdAndStatus(currentUserId, InvitationStatus.PENDING)
                .stream()
                .map(inv -> {
                    String groupName = groupRepository.findById(inv.getGroupId())
                            .map(g -> g.getName())
                            .orElse("Unknown Group");
                    String inviterNickname = userRepository.findById(inv.getInviterId())
                            .map(User::getNickname)
                            .orElse("Unknown User");
                    return new InvitationInfo(
                            inv.getId(),
                            inv.getGroupId(),
                            groupName,
                            inv.getInviterId(),
                            inviterNickname,
                            inv.getInviteeId(),
                            inv.getCreatedAt()
                    );
                })
                .toList();
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication is missing");
        }
        return (Long) authentication.getPrincipal();
    }
}
