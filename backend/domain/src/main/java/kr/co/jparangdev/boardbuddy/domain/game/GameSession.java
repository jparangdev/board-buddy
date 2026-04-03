package kr.co.jparangdev.boardbuddy.domain.game;

import java.time.Instant;
import java.util.List;

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
    /** Comma-separated points per rank (e.g. "10,7,5,3"). Null when not applicable. */
    private String rankPoints;

    @Builder
    public GameSession(Long id, Long groupId, Long gameId, Long customGameId,
                       Instant playedAt, Instant createdAt,
                       ScoreStrategy scoreStrategy, int winnerCount, int winPoints, int losePoints,
                       String rankPoints) {
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
        this.rankPoints = rankPoints;
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
                .rankPoints(serializeRankPoints(config.rankPoints()))
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
                .rankPoints(serializeRankPoints(config.rankPoints()))
                .build();
    }

    private static String serializeRankPoints(List<Integer> rankPoints) {
        if (rankPoints == null || rankPoints.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rankPoints.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(rankPoints.get(i));
        }
        return sb.toString();
    }
}
