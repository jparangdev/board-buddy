package kr.co.jparangdev.boardbuddy.application.group.usecase;

import kr.co.jparangdev.boardbuddy.domain.group.Group;
import kr.co.jparangdev.boardbuddy.domain.group.GroupMember;
import kr.co.jparangdev.boardbuddy.domain.user.User;

import java.util.List;

public interface GroupManagementUseCase {
    /**
     * 모임 생성 (현재 로그인 사용자가 owner, 자동으로 멤버에 추가)
     */
    Group createGroup(String name);

    /**
     * 멤버 초대 (owner만 가능, userTag = 닉네임#discriminator)
     */
    GroupMember inviteMember(Long groupId, String userTag);

    /**
     * 모임 멤버 조회 (모임 멤버만 접근 가능)
     */
    List<User> getGroupMembers(Long groupId);

    /**
     * 모임 상세 조회 (모임 멤버만 접근 가능)
     */
    Group getGroupDetail(Long groupId);

    /**
     * 내가 속한 모임 목록 조회
     */
    List<Group> getMyGroups();
}
