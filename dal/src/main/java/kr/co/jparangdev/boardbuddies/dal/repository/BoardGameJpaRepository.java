package kr.co.jparangdev.boardbuddies.dal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.jparangdev.boardbuddies.dal.entity.BoardGameJpaEntity;

@Repository
public interface BoardGameJpaRepository extends JpaRepository<BoardGameJpaEntity, Long> {
    List<BoardGameJpaEntity> findByCategory(BoardGameJpaEntity.Category category);
}
