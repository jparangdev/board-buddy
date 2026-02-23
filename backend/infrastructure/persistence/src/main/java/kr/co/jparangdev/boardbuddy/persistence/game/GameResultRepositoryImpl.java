package kr.co.jparangdev.boardbuddy.persistence.game;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import kr.co.jparangdev.boardbuddy.domain.game.GameResult;
import kr.co.jparangdev.boardbuddy.domain.game.repository.GameResultRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GameResultRepositoryImpl implements GameResultRepository {

    private final GameResultJpaRepository jpaRepository;
    private final GameResultMapper mapper;

    @Override
    public List<GameResult> saveAll(List<GameResult> results) {
        List<GameResultJpaEntity> entities = results.stream()
            .map(mapper::toEntity)
            .toList();
        return jpaRepository.saveAll(entities).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<GameResult> findAllBySessionId(Long sessionId) {
        return jpaRepository.findAllBySessionId(sessionId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<GameResult> findAllByUserId(Long userId) {
        return jpaRepository.findAllByUserId(userId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<GameResult> findAllBySessionIds(List<Long> sessionIds) {
        if (sessionIds.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findAllBySessionIdIn(sessionIds).stream()
            .map(mapper::toDomain)
            .toList();
    }
}
