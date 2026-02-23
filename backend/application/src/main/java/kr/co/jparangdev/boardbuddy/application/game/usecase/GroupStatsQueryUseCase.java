package kr.co.jparangdev.boardbuddy.application.game.usecase;

import kr.co.jparangdev.boardbuddy.domain.game.GroupStats;

public interface GroupStatsQueryUseCase {
    GroupStats getGroupStats(Long groupId);
}
