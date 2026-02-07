package kr.co.jparangdev.boardbuddy.application.group.usecase;

import java.util.List;

import kr.co.jparangdev.boardbuddy.domain.user.User;

public interface GetGroupMembersUseCase {
    List<User> getGroupMembers(Long groupId);
}
