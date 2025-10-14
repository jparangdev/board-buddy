package kr.co.jparangdev.boardbuddies.dal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddies.application.repostory.BoardRepository;
import kr.co.jparangdev.boardbuddies.dal.entity.BoardGameJpaEntity;
import kr.co.jparangdev.boardbuddies.dal.mapper.BoardGameMapper;
import kr.co.jparangdev.boardbuddies.domain.entity.BoardGame;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepository {

    private final BoardGameJpaRepository boardGameJpaRepository;
    private final BoardGameMapper mapper;

    @Override
    public BoardGame save(BoardGame boardGame) {
        BoardGameJpaEntity saved = boardGameJpaRepository.save(mapper.toEntity(boardGame));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<BoardGame> findById(Long id) {
        return boardGameJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<BoardGame> findAll() {
        return boardGameJpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        boardGameJpaRepository.deleteById(id);
    }

    @Override
    public List<BoardGame> findByCategory(BoardGame.Category category) {
        BoardGameJpaEntity.Category c = BoardGameJpaEntity.Category.valueOf(category.name());
        return boardGameJpaRepository.findByCategory(c).stream().map(mapper::toDomain).toList();
    }
}
