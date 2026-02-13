package kr.co.jparangdev.boardbuddy.application.group.usecase;

import java.util.List;

import kr.co.jparangdev.boardbuddy.domain.group.Group;

public interface GroupCommandUseCase {
    Group createGroup(String name, List<Long> memberIds);

    void deleteGroup(Long groupId);
}
