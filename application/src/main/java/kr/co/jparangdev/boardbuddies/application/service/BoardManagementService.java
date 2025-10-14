package kr.co.jparangdev.boardbuddies.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import kr.co.jparangdev.boardbuddies.application.repostory.BoardRepository;
import kr.co.jparangdev.boardbuddies.application.usecases.BoardManagementUseCase;
import kr.co.jparangdev.boardbuddies.domain.entity.BoardGame;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardManagementService implements BoardManagementUseCase {

    private final BoardRepository boardRepository;

    @Override
    public BoardGame create(BoardGame board) {
        return boardRepository.save(board);
    }

    @Override
    public Optional<BoardGame> getById(Long id) {
        return boardRepository.findById(id);
    }

    @Override
    public List<BoardGame> getAll() {
        return boardRepository.findAll();
    }

    @Override
    public BoardGame update(Long id, BoardGame board) {
        return boardRepository.findById(id)
                .map(existing -> {
                    existing.update(board.getName(), board.getDescription(), board.getMinPlayers(), board.getMaxPlayers(), board.getCategory());
                    return boardRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Board game not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    @Override
    public List<BoardGame> searchByCategory(BoardGame.Category category) {
        return boardRepository.findByCategory(category);
    }
}
