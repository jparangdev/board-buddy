package kr.co.jparangdev.boardbuddies.application.repostory;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddies.domain.entity.GameSession;

public interface GameSessionRepository {
    GameSession save(GameSession gameSession);
    Optional<GameSession> findById(Long id);
    List<GameSession> findAll();
    void deleteById(Long id);
    List<GameSession> findByLocationContaining(String location);
}
