package kr.co.jparangdev.boardbuddy.persistence.group;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.co.jparangdev.boardbuddy.domain.group.Group;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepository {

    private final GroupJpaRepository jpaRepository;
    private final GroupMapper mapper;

    @Override
    public Group save(Group group) {
        GroupJpaEntity entity = mapper.toEntity(group);
        GroupJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Group> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Group> findAllByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findAllByIdIn(ids).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
