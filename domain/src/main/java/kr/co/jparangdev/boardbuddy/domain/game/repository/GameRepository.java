package kr.co.jparangdev.boardbuddy.domain.game.repository;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddy.domain.game.Game;

public interface GameRepository {
    Game save(Game game);
    Optional<Game> findById(Long id);
    List<Game> findAll();
    boolean existsByName(String name);
}
