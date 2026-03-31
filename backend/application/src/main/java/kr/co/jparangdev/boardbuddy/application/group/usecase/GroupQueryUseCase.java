package kr.co.jparangdev.boardbuddy.application.group.usecase;

import java.util.List;

import kr.co.jparangdev.boardbuddy.application.group.dto.GroupMemberInfo;
import kr.co.jparangdev.boardbuddy.domain.group.Group;

public interface GroupQueryUseCase {
    Group getGroupDetail(Long groupId);

    List<Group> getMyGroups();

    List<GroupMemberInfo> getGroupMembers(Long groupId);
}
