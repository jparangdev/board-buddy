package kr.co.jparangdev.boardbuddy.persistence.game;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.co.jparangdev.boardbuddy.domain.game.CustomGame;
import kr.co.jparangdev.boardbuddy.domain.game.repository.CustomGameRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomGameRepositoryImpl implements CustomGameRepository {

    private final CustomGameJpaRepository jpaRepository;
    private final CustomGameMapper mapper;

    @Override
    public CustomGame save(CustomGame customGame) {
        CustomGameJpaEntity entity = mapper.toEntity(customGame);
        CustomGameJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CustomGame> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<CustomGame> findAllByGroupId(Long groupId) {
        return jpaRepository.findAllByGroupId(groupId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsByGroupIdAndName(Long groupId, String name) {
        return jpaRepository.existsByGroupIdAndName(groupId, name);
    }
}
