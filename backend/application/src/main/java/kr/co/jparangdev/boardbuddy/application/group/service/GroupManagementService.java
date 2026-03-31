package kr.co.jparangdev.boardbuddy.application.group.service;

import java.util.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.group.dto.GroupMemberInfo;
import kr.co.jparangdev.boardbuddy.application.group.dto.GroupMemberStatus;
import kr.co.jparangdev.boardbuddy.application.group.usecase.*;
import kr.co.jparangdev.boardbuddy.domain.group.Group;
import kr.co.jparangdev.boardbuddy.domain.group.GroupMember;
import kr.co.jparangdev.boardbuddy.domain.group.exception.GroupNotFoundException;
import kr.co.jparangdev.boardbuddy.domain.group.exception.NotGroupOwnerException;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupMemberRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupRepository;
import kr.co.jparangdev.boardbuddy.domain.invitation.Invitation;
import kr.co.jparangdev.boardbuddy.domain.invitation.InvitationStatus;
import kr.co.jparangdev.boardbuddy.domain.invitation.repository.InvitationRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.exception.UserNotFoundException;
import kr.co.jparangdev.boardbuddy.domain.user.exception.UserNotGroupMemberException;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupManagementService implements GroupCommandUseCase, GroupQueryUseCase, UpdateGroupOrderUseCase {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Group createGroup(String name, List<Long> memberIds) {
        Long currentUserId = getCurrentUserId();

        Group group = Group.create(name, currentUserId);
        Group savedGroup = groupRepository.save(group);

        // Owner is an active member
        groupMemberRepository.save(GroupMember.create(savedGroup.getId(), currentUserId));

        // Invite other users as pending
        Set<Long> uniqueInviteeIds = new LinkedHashSet<>(memberIds);
        uniqueInviteeIds.remove(currentUserId);
        for (Long inviteeId : uniqueInviteeIds) {
            if (!userRepository.existsById(inviteeId)) {
                throw new UserNotFoundException(inviteeId);
            }
            Invitation invitation = Invitation.create(savedGroup.getId(), currentUserId, inviteeId);
            invitationRepository.save(invitation);
        }

        return savedGroup;
    }

    @Override
    public List<GroupMemberInfo> getGroupMembers(Long groupId) {
        Long currentUserId = getCurrentUserId();

        // 1. 모임 존재 확인
        if (groupRepository.findById(groupId).isEmpty()) {
            throw new GroupNotFoundException(groupId);
        }

        // 2. 현재 사용자가 멤버인지 확인
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new UserNotGroupMemberException(groupId, currentUserId);
        }

        // 3. 멤버 목록 조회
        List<GroupMember> members = groupMemberRepository.findAllByGroupId(groupId);
        Map<Long, GroupMember> memberMap = new HashMap<>();
        for (GroupMember member : members) {
            memberMap.put(member.getUserId(), member);
        }

        List<GroupMemberInfo> results = new ArrayList<>();
        for (GroupMember member : members) {
            User user = userRepository.findById(member.getUserId())
                    .orElseThrow(() -> new UserNotFoundException(member.getUserId()));
            results.add(new GroupMemberInfo(
                    user.getId(),
                    user.getNickname(),
                    user.getDiscriminator(),
                    user.getUserTag(),
                    member.getJoinedAt(),
                    GroupMemberStatus.ACTIVE
            ));
        }

        // 4. 초대 중인 멤버 포함
        List<Invitation> pendingInvitations = invitationRepository.findAllByGroupIdAndStatus(groupId, InvitationStatus.PENDING);
        for (Invitation invitation : pendingInvitations) {
            if (memberMap.containsKey(invitation.getInviteeId())) {
                continue;
            }
            User user = userRepository.findById(invitation.getInviteeId())
                    .orElseThrow(() -> new UserNotFoundException(invitation.getInviteeId()));
            results.add(new GroupMemberInfo(
                    user.getId(),
                    user.getNickname(),
                    user.getDiscriminator(),
                    user.getUserTag(),
                    null,
                    GroupMemberStatus.PENDING
            ));
        }

        return results;
    }

    @Override
    public Group getGroupDetail(Long groupId) {
        Long currentUserId = getCurrentUserId();

        // 1. 모임 존재 확인
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        // 2. 현재 사용자가 멤버인지 확인
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new UserNotGroupMemberException(groupId, currentUserId);
        }

        return group;
    }

    @Override
    public List<Group> getMyGroups() {
        Long currentUserId = getCurrentUserId();

        // 1. 사용자가 속한 모든 GroupMember 조회 (순서대로)
        List<GroupMember> memberships = groupMemberRepository.findAllByUserIdOrderByDisplayOrderAsc(currentUserId);

        // 2. 그룹 ID 목록 추출 (순서 유지)
        List<Long> groupIds = memberships.stream()
                .map(GroupMember::getGroupId)
                .toList();

        // 3. 그룹 정보 조회 후 순서에 맞게 정렬
        List<Group> groups = groupRepository.findAllByIds(groupIds);
        Map<Long, Group> groupMap = new HashMap<>();
        groups.forEach(g -> groupMap.put(g.getId(), g));

        return groupIds.stream()
                .map(groupMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional
    public void updateGroupOrder(List<Long> groupIds) {
        Long currentUserId = getCurrentUserId();

        for (int i = 0; i < groupIds.size(); i++) {
            Long groupId = groupIds.get(i);
            GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                    .orElseThrow(() -> new UserNotGroupMemberException(groupId, currentUserId));

            GroupMember updatedMember = GroupMember.builder()
                    .id(member.getId())
                    .groupId(member.getGroupId())
                    .userId(member.getUserId())
                    .joinedAt(member.getJoinedAt())
                    .displayOrder(i)
                    .build();

            groupMemberRepository.save(updatedMember);
        }
    }

    @Override
    @Transactional
    public void deleteGroup(Long groupId) {
        Long currentUserId = getCurrentUserId();

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (!group.isOwner(currentUserId)) {
            throw new NotGroupOwnerException(groupId, currentUserId);
        }

        invitationRepository.deleteAllByGroupId(groupId);
        groupMemberRepository.deleteAllByGroupId(groupId);
        groupRepository.deleteById(groupId);
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication is missing");
        }
        return (Long) authentication.getPrincipal();
    }
}
