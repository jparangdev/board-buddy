package kr.co.jparangdev.boardbuddy.application.group.service;

import java.util.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.group.exception.GroupNotFoundException;
import kr.co.jparangdev.boardbuddy.application.group.exception.NotGroupOwnerException;
import kr.co.jparangdev.boardbuddy.application.group.usecase.*;
import kr.co.jparangdev.boardbuddy.application.user.exception.UserNotFoundException;
import kr.co.jparangdev.boardbuddy.application.user.exception.UserNotGroupMemberException;
import kr.co.jparangdev.boardbuddy.domain.group.Group;
import kr.co.jparangdev.boardbuddy.domain.group.GroupMember;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupMemberRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupManagementService implements CreateGroupUseCase, GetGroupMembersUseCase,
        GetGroupDetailUseCase, GetMyGroupsUseCase, DeleteGroupUseCase {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Group createGroup(String name, List<Long> memberIds) {
        Long currentUserId = getCurrentUserId();

        Group group = Group.create(name, currentUserId);
        Group savedGroup = groupRepository.save(group);

        // Use Set to deduplicate; owner is always first
        Set<Long> uniqueMemberIds = new LinkedHashSet<>();
        uniqueMemberIds.add(currentUserId);
        uniqueMemberIds.addAll(memberIds);

        for (Long userId : uniqueMemberIds) {
            GroupMember member = GroupMember.create(savedGroup.getId(), userId);
            groupMemberRepository.save(member);
        }

        return savedGroup;
    }

    @Override
    public List<User> getGroupMembers(Long groupId) {
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
        List<Long> userIds = members.stream()
                .map(GroupMember::getUserId)
                .toList();

        return userIds.stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId)))
                .toList();
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

        // 1. 사용자가 속한 모든 GroupMember 조회
        List<GroupMember> memberships = groupMemberRepository.findAllByUserId(currentUserId);

        // 2. 그룹 ID 목록 추출
        List<Long> groupIds = memberships.stream()
                .map(GroupMember::getGroupId)
                .toList();

        // 3. 그룹 정보 조회
        return groupRepository.findAllByIds(groupIds);
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
