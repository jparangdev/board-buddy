package kr.co.jparangdev.boardbuddy.persistence.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GameSessionJpaRepository extends JpaRepository<GameSessionJpaEntity, Long> {
    List<GameSessionJpaEntity> findAllByGroupId(Long groupId);
}
