package kr.co.jparangdev.boardbuddy.persistence.group;

import kr.co.jparangdev.boardbuddy.application.group.service.GroupMemberRepository;
import kr.co.jparangdev.boardbuddy.domain.group.GroupMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            .collect(Collectors.toList());
    }

    @Override
    public List<GroupMember> findAllByUserId(Long userId) {
        return jpaRepository.findAllByUserId(userId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByGroupIdAndUserId(Long groupId, Long userId) {
        return jpaRepository.existsByGroupIdAndUserId(groupId, userId);
    }
}
