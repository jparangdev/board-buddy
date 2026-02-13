package kr.co.jparangdev.boardbuddy.domain.game;

import java.time.LocalDateTime;

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
    private LocalDateTime playedAt;
    private LocalDateTime createdAt;

    @Builder
    public GameSession(Long id, Long groupId, Long gameId,
                       LocalDateTime playedAt, LocalDateTime createdAt) {
        this.id = id;
        this.groupId = groupId;
        this.gameId = gameId;
        this.playedAt = playedAt;
        this.createdAt = createdAt;
    }

    public static GameSession create(Long groupId, Long gameId, LocalDateTime playedAt) {
        return GameSession.builder()
                .groupId(groupId)
                .gameId(gameId)
                .playedAt(playedAt)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
