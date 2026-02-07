package kr.co.jparangdev.boardbuddy.persistence.group;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberJpaRepository extends JpaRepository<GroupMemberJpaEntity, Long> {
    Optional<GroupMemberJpaEntity> findByGroupIdAndUserId(Long groupId, Long userId);
    List<GroupMemberJpaEntity> findAllByGroupId(Long groupId);
    List<GroupMemberJpaEntity> findAllByUserId(Long userId);
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    void deleteAllByGroupId(Long groupId);
}
