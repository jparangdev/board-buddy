package kr.co.jparangdev.boardbuddy.persistence.game;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.domain.game.CustomGame;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;

@Component
public class CustomGameMapper {

    public CustomGameJpaEntity toEntity(CustomGame customGame) {
        return new CustomGameJpaEntity(
            customGame.getId(),
            customGame.getGroupId(),
            customGame.getName(),
            customGame.getNameKo(),
            customGame.getNameEn(),
            customGame.getMinPlayers(),
            customGame.getMaxPlayers(),
            customGame.getScoreStrategy().name(),
            customGame.getCreatedAt()
        );
    }

    public CustomGame toDomain(CustomGameJpaEntity entity) {
        return CustomGame.builder()
            .id(entity.getId())
            .groupId(entity.getGroupId())
            .name(entity.getName())
            .nameKo(entity.getNameKo())
            .nameEn(entity.getNameEn())
            .minPlayers(entity.getMinPlayers())
            .maxPlayers(entity.getMaxPlayers())
            .scoreStrategy(ScoreStrategy.valueOf(entity.getScoreStrategy()))
            .createdAt(entity.getCreatedAt())
            .build();
    }
}
