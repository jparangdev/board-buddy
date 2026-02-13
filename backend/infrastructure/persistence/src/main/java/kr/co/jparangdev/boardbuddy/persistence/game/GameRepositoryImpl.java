package kr.co.jparangdev.boardbuddy.persistence.game;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepository {

    private final GameJpaRepository jpaRepository;
    private final GameMapper mapper;

    @Override
    public Game save(Game game) {
        GameJpaEntity entity = mapper.toEntity(game);
        GameJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Game> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Game> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
}
