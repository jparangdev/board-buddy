package kr.co.jparangdev.boardbuddy.persistence.game;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.domain.game.GameResult;

@Component
public class GameResultMapper {

    public GameResultJpaEntity toEntity(GameResult result) {
        return new GameResultJpaEntity(
            result.getId(),
            result.getSessionId(),
            result.getUserId(),
            result.getScore(),
            result.getRank()
        );
    }

    public GameResult toDomain(GameResultJpaEntity entity) {
        return GameResult.builder()
            .id(entity.getId())
            .sessionId(entity.getSessionId())
            .userId(entity.getUserId())
            .score(entity.getScore())
            .rank(entity.getRank())
            .build();
    }
}
