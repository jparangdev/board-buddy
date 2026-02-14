package kr.co.jparangdev.boardbuddy.persistence.game;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.domain.game.GameSession;

@Component
public class GameSessionMapper {

    public GameSessionJpaEntity toEntity(GameSession session) {
        return new GameSessionJpaEntity(
            session.getId(),
            session.getGroupId(),
            session.getGameId(),
            session.getCustomGameId(),
            session.getPlayedAt(),
            session.getCreatedAt()
        );
    }

    public GameSession toDomain(GameSessionJpaEntity entity) {
        return GameSession.builder()
            .id(entity.getId())
            .groupId(entity.getGroupId())
            .gameId(entity.getGameId())
            .customGameId(entity.getCustomGameId())
            .playedAt(entity.getPlayedAt())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}
