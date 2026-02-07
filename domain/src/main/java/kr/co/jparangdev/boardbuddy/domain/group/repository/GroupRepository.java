package kr.co.jparangdev.boardbuddy.domain.group.repository;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddy.domain.group.Group;

public interface GroupRepository {
    Group save(Group group);
    Optional<Group> findById(Long id);
    List<Group> findAllByIds(List<Long> ids);
    void deleteById(Long id);
}
