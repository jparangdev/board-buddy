package kr.co.jparangdev.boardbuddy.application.group.usecase;

import java.util.List;

public interface UpdateGroupOrderUseCase {
    void updateGroupOrder(List<Long> groupIds);
}
