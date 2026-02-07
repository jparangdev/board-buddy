package kr.co.jparangdev.boardbuddy.application.group.usecase;

import kr.co.jparangdev.boardbuddy.domain.group.GroupMember;

public interface InviteMemberUseCase {
    GroupMember inviteMember(Long groupId, String userTag);
}
