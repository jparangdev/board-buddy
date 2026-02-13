package kr.co.jparangdev.boardbuddy.application.group.usecase;

import java.util.List;

import kr.co.jparangdev.boardbuddy.domain.group.Group;
import kr.co.jparangdev.boardbuddy.domain.user.User;

public interface GroupQueryUseCase {
    Group getGroupDetail(Long groupId);

    List<Group> getMyGroups();

    List<User> getGroupMembers(Long groupId);
}
