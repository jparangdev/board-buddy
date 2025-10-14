package kr.co.jparangdev.boardbuddies.dal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddies.application.repostory.GameSessionRepository;
import kr.co.jparangdev.boardbuddies.dal.mapper.GameSessionMapper;
import kr.co.jparangdev.boardbuddies.domain.entity.GameSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GameSessionRepositoryImpl implements GameSessionRepository {

    private final GameSessionJpaRepository jpaRepository;
    private final GameSessionMapper mapper;

    @Override
    public GameSession save(GameSession gameSession) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(gameSession)));
    }

    @Override
    public Optional<GameSession> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<GameSession> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<GameSession> findByLocationContaining(String location) {
        return jpaRepository.findByLocationContaining(location).stream().map(mapper::toDomain).toList();
    }
}
