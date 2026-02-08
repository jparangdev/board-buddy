package kr.co.jparangdev.boardbuddy.persistence.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GameResultJpaRepository extends JpaRepository<GameResultJpaEntity, Long> {
    List<GameResultJpaEntity> findAllBySessionId(Long sessionId);
    List<GameResultJpaEntity> findAllByUserId(Long userId);
}
