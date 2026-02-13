package kr.co.jparangdev.boardbuddy.persistence.group;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupJpaRepository extends JpaRepository<GroupJpaEntity, Long> {
    List<GroupJpaEntity> findAllByIdIn(List<Long> ids);
}
