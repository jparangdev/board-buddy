package kr.co.jparangdev.boardbuddies.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import kr.co.jparangdev.boardbuddies.application.repostory.GameSessionRepository;
import kr.co.jparangdev.boardbuddies.application.usecases.GameSessionUseCase;
import kr.co.jparangdev.boardbuddies.domain.entity.GameSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GameSessionService implements GameSessionUseCase {

    private final GameSessionRepository gameSessionRepository;

    @Override
    public GameSession create(GameSession session) {
        return gameSessionRepository.save(session);
    }

    @Override
    public Optional<GameSession> getById(Long id) {
        return gameSessionRepository.findById(id);
    }

    @Override
    public List<GameSession> getAll() {
        return gameSessionRepository.findAll();
    }

    @Override
    public GameSession update(Long id, GameSession session) {
        return gameSessionRepository.findById(id)
                .map(existing -> {
                    existing.update(session.getMaxPlayers(), session.getScheduledDate(), session.getLocation(), session.getDescription());
                    return gameSessionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Game session not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        gameSessionRepository.deleteById(id);
    }

    @Override
    public List<GameSession> searchByLocation(String location) {
        return gameSessionRepository.findByLocationContaining(location);
    }

    @Override
    public GameSession join(Long sessionId, Long userId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Game session not found with id: " + sessionId));
        session.join(userId);
        return gameSessionRepository.save(session);
    }

    @Override
    public GameSession leave(Long sessionId, Long userId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Game session not found with id: " + sessionId));
        session.leave(userId);
        return gameSessionRepository.save(session);
    }
}
