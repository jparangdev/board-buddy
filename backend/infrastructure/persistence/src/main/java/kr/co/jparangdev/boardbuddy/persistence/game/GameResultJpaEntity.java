package kr.co.jparangdev.boardbuddy.persistence.game;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_results",
        uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameResultJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column
    private Integer score;

    @Column(nullable = false)
    private boolean won;

    @Column(nullable = false)
    private int rank;

    @Column(name = "team_id")
    private Integer teamId;

    public GameResultJpaEntity(Long id, Long sessionId, Long userId, Integer score, boolean won, int rank, Integer teamId) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.score = score;
        this.won = won;
        this.rank = rank;
        this.teamId = teamId;
    }
}
