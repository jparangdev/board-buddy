package kr.co.jparangdev.boardbuddies.application.repostory;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddies.domain.entity.BoardGame;

public interface BoardRepository {
    BoardGame save(BoardGame boardGame);
    Optional<BoardGame> findById(Long id);
    List<BoardGame> findAll();
    void deleteById(Long id);
    List<BoardGame> findByCategory(BoardGame.Category category);
}
