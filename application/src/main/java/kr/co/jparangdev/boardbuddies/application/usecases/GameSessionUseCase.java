package kr.co.jparangdev.boardbuddies.application.usecases;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddies.domain.entity.GameSession;

public interface GameSessionUseCase {
    GameSession create(GameSession session);
    Optional<GameSession> getById(Long id);
    List<GameSession> getAll();
    GameSession update(Long id, GameSession session);
    void delete(Long id);
    List<GameSession> searchByLocation(String location);

    // participation
    GameSession join(Long sessionId, Long userId);
    GameSession leave(Long sessionId, Long userId);
}
