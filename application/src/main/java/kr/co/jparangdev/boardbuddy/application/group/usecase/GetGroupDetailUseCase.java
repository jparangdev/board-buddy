package kr.co.jparangdev.boardbuddy.application.group.usecase;

import kr.co.jparangdev.boardbuddy.domain.group.Group;

public interface GetGroupDetailUseCase {
    Group getGroupDetail(Long groupId);
}
