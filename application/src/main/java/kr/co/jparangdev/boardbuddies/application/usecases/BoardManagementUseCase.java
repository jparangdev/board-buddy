package kr.co.jparangdev.boardbuddies.application.usecases;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddies.domain.entity.BoardGame;

public interface BoardManagementUseCase {
    BoardGame create(BoardGame board);
    Optional<BoardGame> getById(Long id);
    List<BoardGame> getAll();
    BoardGame update(Long id, BoardGame board);
    void delete(Long id);
    List<BoardGame> searchByCategory(BoardGame.Category category);
}
