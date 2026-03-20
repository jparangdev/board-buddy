package kr.co.jparangdev.boardbuddy.domain.game;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameSession {
    private Long id;
    private Long groupId;
    private Long gameId;
    private Long customGameId;
    private Instant playedAt;
    private Instant createdAt;
    private ScoreStrategy scoreStrategy;
    private int winnerCount;
    private int winPoints;
    private int losePoints;

    @Builder
    public GameSession(Long id, Long groupId, Long gameId, Long customGameId,
                       Instant playedAt, Instant createdAt,
                       ScoreStrategy scoreStrategy, int winnerCount, int winPoints, int losePoints) {
        this.id = id;
        this.groupId = groupId;
        this.gameId = gameId;
        this.customGameId = customGameId;
        this.playedAt = playedAt;
        this.createdAt = createdAt;
        this.scoreStrategy = scoreStrategy;
        this.winnerCount = winnerCount;
        this.winPoints = winPoints;
        this.losePoints = losePoints;
    }

    public static GameSession create(Long groupId, Long gameId, Instant playedAt, SessionConfig config) {
        return GameSession.builder()
                .groupId(groupId)
                .gameId(gameId)
                .playedAt(playedAt)
                .createdAt(Instant.now())
                .scoreStrategy(config.scoreStrategy())
                .winnerCount(config.winnerCount())
                .winPoints(config.winPoints())
                .losePoints(config.losePoints())
                .build();
    }

    public static GameSession createWithCustomGame(Long groupId, Long customGameId, Instant playedAt, SessionConfig config) {
        return GameSession.builder()
                .groupId(groupId)
                .customGameId(customGameId)
                .playedAt(playedAt)
                .createdAt(Instant.now())
                .scoreStrategy(config.scoreStrategy())
                .winnerCount(config.winnerCount())
                .winPoints(config.winPoints())
                .losePoints(config.losePoints())
                .build();
    }
}
