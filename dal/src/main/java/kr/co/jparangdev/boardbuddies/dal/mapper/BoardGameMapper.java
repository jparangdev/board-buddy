package kr.co.jparangdev.boardbuddies.dal.mapper;

import org.mapstruct.*;

import kr.co.jparangdev.boardbuddies.dal.entity.BoardGameJpaEntity;
import kr.co.jparangdev.boardbuddies.domain.entity.BoardGame;

@Mapper(componentModel = "spring")
public interface BoardGameMapper {

    @Mappings({
            @Mapping(target = "category", expression = "java(mapCategoryToEntity(board.getCategory()))")
    })
    BoardGameJpaEntity toEntity(BoardGame board);

    @Mappings({
            @Mapping(target = "category", expression = "java(mapCategoryToDomain(entity.getCategory()))")
    })
    BoardGame toDomain(BoardGameJpaEntity entity);

    default BoardGameJpaEntity.Category mapCategoryToEntity(BoardGame.Category category) {
        return category == null ? null : BoardGameJpaEntity.Category.valueOf(category.name());
    }

    default BoardGame.Category mapCategoryToDomain(BoardGameJpaEntity.Category category) {
        return category == null ? null : BoardGame.Category.valueOf(category.name());
    }
}
