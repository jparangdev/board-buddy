package kr.co.jparangdev.boardbuddy.persistence.group;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.co.jparangdev.boardbuddy.domain.group.GroupMember;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GroupMemberRepositoryImpl implements GroupMemberRepository {

    private final GroupMemberJpaRepository jpaRepository;
    private final GroupMemberMapper mapper;

    @Override
    public GroupMember save(GroupMember groupMember) {
        GroupMemberJpaEntity entity = mapper.toEntity(groupMember);
        GroupMemberJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId) {
        return jpaRepository.findByGroupIdAndUserId(groupId, userId).map(mapper::toDomain);
    }

    @Override
    public List<GroupMember> findAllByGroupId(Long groupId) {
        return jpaRepository.findAllByGroupId(groupId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<GroupMember> findAllByUserId(Long userId) {
        return jpaRepository.findAllByUserId(userId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsByGroupIdAndUserId(Long groupId, Long userId) {
        return jpaRepository.existsByGroupIdAndUserId(groupId, userId);
    }

    @Override
    public void deleteAllByGroupId(Long groupId) {
        jpaRepository.deleteAllByGroupId(groupId);
    }
}
