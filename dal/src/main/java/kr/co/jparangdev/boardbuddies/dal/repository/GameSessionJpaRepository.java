package kr.co.jparangdev.boardbuddies.dal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.jparangdev.boardbuddies.dal.entity.GameSessionJpaEntity;

@Repository
public interface GameSessionJpaRepository extends JpaRepository<GameSessionJpaEntity, Long> {
    List<GameSessionJpaEntity> findByLocationContaining(String location);
}
