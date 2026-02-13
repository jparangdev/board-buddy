package kr.co.jparangdev.boardbuddy.domain.group.repository;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddy.domain.group.GroupMember;

public interface GroupMemberRepository {
    GroupMember save(GroupMember groupMember);
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
    List<GroupMember> findAllByGroupId(Long groupId);
    List<GroupMember> findAllByUserId(Long userId);
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    void deleteAllByGroupId(Long groupId);
}
