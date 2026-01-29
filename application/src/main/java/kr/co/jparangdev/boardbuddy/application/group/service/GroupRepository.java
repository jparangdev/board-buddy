package kr.co.jparangdev.boardbuddy.application.group.service;

import kr.co.jparangdev.boardbuddy.domain.group.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    Group save(Group group);
    Optional<Group> findById(Long id);
    List<Group> findAllByIds(List<Long> ids);
}
