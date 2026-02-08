package kr.co.jparangdev.boardbuddy.domain.game.repository;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddy.domain.game.GameSession;

public interface GameSessionRepository {
    GameSession save(GameSession session);
    Optional<GameSession> findById(Long id);
    List<GameSession> findAllByGroupId(Long groupId);
}
