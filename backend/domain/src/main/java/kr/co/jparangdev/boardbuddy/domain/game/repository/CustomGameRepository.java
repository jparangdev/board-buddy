package kr.co.jparangdev.boardbuddy.domain.game.repository;

import java.util.List;
import java.util.Optional;

import kr.co.jparangdev.boardbuddy.domain.game.CustomGame;

public interface CustomGameRepository {
    CustomGame save(CustomGame customGame);
    Optional<CustomGame> findById(Long id);
    List<CustomGame> findAllByGroupId(Long groupId);
    boolean existsByGroupIdAndName(Long groupId, String name);
}
