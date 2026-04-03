package kr.co.jparangdev.boardbuddy.domain.game;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameResult {
    private Long id;
    private Long sessionId;
    private Long userId;
    private Integer score;
    private boolean won;
    private int rank;
    /** Nullable. Players with the same teamId form a team within this session. */
    private Integer teamId;

    @Builder
    public GameResult(Long id, Long sessionId, Long userId, Integer score, boolean won, int rank, Integer teamId) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.score = score;
        this.won = won;
        this.rank = rank;
        this.teamId = teamId;
    }

    public static GameResult create(Long sessionId, Long userId, Integer score, boolean won, int rank, Integer teamId) {
        return GameResult.builder()
                .sessionId(sessionId)
                .userId(userId)
                .score(score)
                .won(won)
                .rank(rank)
                .teamId(teamId)
                .build();
    }
}
