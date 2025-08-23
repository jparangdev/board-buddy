package kr.co.jparangdev.boardbuddies.dal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.jparangdev.boardbuddies.dal.entity.UserJpaEntity;

/**
 * Spring Data JPA repository for UserEntity
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    /**
     * Find a user by username
     * @param username the username to search for
     * @return the user if found
     */
    Optional<UserJpaEntity> findByUsername(String username);

    /**
     * Find a user by email
     * @param email the email to search for
     * @return the user if found
     */
    Optional<UserJpaEntity> findByEmail(String email);
}
