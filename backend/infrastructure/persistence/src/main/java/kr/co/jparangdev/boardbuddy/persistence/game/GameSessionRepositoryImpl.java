package kr.co.jparangdev.boardbuddy.persistence.game;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.co.jparangdev.boardbuddy.domain.game.GameSession;
import kr.co.jparangdev.boardbuddy.domain.game.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GameSessionRepositoryImpl implements GameSessionRepository {

    private final GameSessionJpaRepository jpaRepository;
    private final GameSessionMapper mapper;

    @Override
    public GameSession save(GameSession session) {
        GameSessionJpaEntity entity = mapper.toEntity(session);
        GameSessionJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<GameSession> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<GameSession> findAllByGroupId(Long groupId) {
        return jpaRepository.findAllByGroupId(groupId).stream()
            .map(mapper::toDomain)
            .toList();
    }
}
