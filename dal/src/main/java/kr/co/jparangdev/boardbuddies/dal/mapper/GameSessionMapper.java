package kr.co.jparangdev.boardbuddies.dal.mapper;

import org.mapstruct.Mapper;

import kr.co.jparangdev.boardbuddies.dal.entity.GameSessionJpaEntity;
import kr.co.jparangdev.boardbuddies.domain.entity.GameSession;

@Mapper(componentModel = "spring")
public interface GameSessionMapper {
    GameSessionJpaEntity toEntity(GameSession session);
    GameSession toDomain(GameSessionJpaEntity entity);
}
