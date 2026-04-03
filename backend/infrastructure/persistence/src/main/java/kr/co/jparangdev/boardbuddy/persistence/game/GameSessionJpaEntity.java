package kr.co.jparangdev.boardbuddy.persistence.game;

import java.time.Instant;

import jakarta.persistence.*;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_sessions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameSessionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "game_id")
    private Long gameId;

    @Column(name = "custom_game_id")
    private Long customGameId;

    @Column(name = "played_at", nullable = false)
    private Instant playedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "score_strategy", nullable = false)
    private ScoreStrategy scoreStrategy;

    @Column(name = "winner_count", nullable = false)
    private int winnerCount;

    @Column(name = "win_points", nullable = false)
    private int winPoints;

    @Column(name = "lose_points", nullable = false)
    private int losePoints;

    /** Comma-separated points per rank for RANK_SCORE strategy (e.g. "10,7,5,3"). Nullable. */
    @Column(name = "rank_points")
    private String rankPoints;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public GameSessionJpaEntity(Long id, Long groupId, Long gameId, Long customGameId,
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
}
