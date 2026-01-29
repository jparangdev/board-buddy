package kr.co.jparangdev.boardbuddy.persistence.group;

import kr.co.jparangdev.boardbuddy.application.group.service.GroupRepository;
import kr.co.jparangdev.boardbuddy.domain.group.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            .collect(Collectors.toList());
    }
}
