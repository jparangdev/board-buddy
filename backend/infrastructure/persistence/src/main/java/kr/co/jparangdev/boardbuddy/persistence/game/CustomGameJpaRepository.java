package kr.co.jparangdev.boardbuddy.persistence.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomGameJpaRepository extends JpaRepository<CustomGameJpaEntity, Long> {
    List<CustomGameJpaEntity> findAllByGroupId(Long groupId);
    boolean existsByGroupIdAndName(Long groupId, String name);
}
