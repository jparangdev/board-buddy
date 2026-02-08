package kr.co.jparangdev.boardbuddy.persistence.game;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;

@Component
public class GameMapper {

    public GameJpaEntity toEntity(Game game) {
        return new GameJpaEntity(
            game.getId(),
            game.getName(),
            game.getMinPlayers(),
            game.getMaxPlayers(),
            game.getScoreStrategy().name(),
            game.getCreatedAt()
        );
    }

    public Game toDomain(GameJpaEntity entity) {
        return Game.builder()
            .id(entity.getId())
            .name(entity.getName())
            .minPlayers(entity.getMinPlayers())
            .maxPlayers(entity.getMaxPlayers())
            .scoreStrategy(ScoreStrategy.valueOf(entity.getScoreStrategy()))
            .createdAt(entity.getCreatedAt())
            .build();
    }
}
