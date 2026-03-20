package kr.co.jparangdev.boardbuddy.persistence.game;

import java.time.LocalDateTime;

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
    private LocalDateTime playedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "score_strategy", nullable = false)
    private ScoreStrategy scoreStrategy;

    @Column(name = "winner_count", nullable = false)
    private int winnerCount;

    @Column(name = "win_points", nullable = false)
    private int winPoints;

    @Column(name = "lose_points", nullable = false)
    private int losePoints;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public GameSessionJpaEntity(Long id, Long groupId, Long gameId, Long customGameId,
                                LocalDateTime playedAt, LocalDateTime createdAt,
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
}
