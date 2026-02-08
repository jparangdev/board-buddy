package kr.co.jparangdev.boardbuddy.persistence.game;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GameJpaRepository extends JpaRepository<GameJpaEntity, Long> {
    boolean existsByName(String name);
}
