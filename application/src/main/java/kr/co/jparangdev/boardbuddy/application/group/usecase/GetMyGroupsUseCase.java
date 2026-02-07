package kr.co.jparangdev.boardbuddy.application.group.usecase;

import java.util.List;

import kr.co.jparangdev.boardbuddy.domain.group.Group;

public interface GetMyGroupsUseCase {
    List<Group> getMyGroups();
}
