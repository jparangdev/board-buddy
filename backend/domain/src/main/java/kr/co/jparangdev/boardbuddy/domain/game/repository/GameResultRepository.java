package kr.co.jparangdev.boardbuddy.domain.game.repository;

import java.util.List;

import kr.co.jparangdev.boardbuddy.domain.game.GameResult;

public interface GameResultRepository {
    List<GameResult> saveAll(List<GameResult> results);
    List<GameResult> findAllBySessionId(Long sessionId);
    List<GameResult> findAllByUserId(Long userId);
    List<GameResult> findAllBySessionIds(List<Long> sessionIds);
}
