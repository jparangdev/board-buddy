package kr.co.jparangdev.boardbuddy.domain.game;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameResult {
    private Long id;
    private Long sessionId;
    private Long userId;
    private Integer score;
    private int rank;

    @Builder
    public GameResult(Long id, Long sessionId, Long userId, Integer score, int rank) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.score = score;
        this.rank = rank;
    }

    public static GameResult create(Long sessionId, Long userId, Integer score, int rank) {
        return GameResult.builder()
                .sessionId(sessionId)
                .userId(userId)
                .score(score)
                .rank(rank)
                .build();
    }
}
