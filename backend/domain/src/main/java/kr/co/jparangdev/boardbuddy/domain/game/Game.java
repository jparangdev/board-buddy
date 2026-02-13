package kr.co.jparangdev.boardbuddy.domain.game;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {
    private Long id;
    private String name;
    private int minPlayers;
    private int maxPlayers;
    private ScoreStrategy scoreStrategy;
    private LocalDateTime createdAt;

    @Builder
    public Game(Long id, String name, int minPlayers, int maxPlayers,
                ScoreStrategy scoreStrategy, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.scoreStrategy = scoreStrategy;
        this.createdAt = createdAt;
    }

    public static Game create(String name, int minPlayers, int maxPlayers, ScoreStrategy scoreStrategy) {
        return Game.builder()
                .name(name)
                .minPlayers(minPlayers)
                .maxPlayers(maxPlayers)
                .scoreStrategy(scoreStrategy)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
