package kr.co.jparangdev.boardbuddy.domain.game;

import java.time.Instant;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {
    private Long id;
    private String name;
    private String nameKo;
    private String nameEn;
    private int minPlayers;
    private int maxPlayers;
    private ScoreStrategy scoreStrategy;
    private Instant createdAt;

    @Builder
    public Game(Long id, String name, String nameKo, String nameEn, int minPlayers, int maxPlayers,
                ScoreStrategy scoreStrategy, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.scoreStrategy = scoreStrategy;
        this.createdAt = createdAt;
    }

    public static Game create(String name, String nameKo, String nameEn, int minPlayers, int maxPlayers, ScoreStrategy scoreStrategy) {
        return Game.builder()
                .name(name)
                .nameKo(nameKo)
                .nameEn(nameEn)
                .minPlayers(minPlayers)
                .maxPlayers(maxPlayers)
                .scoreStrategy(scoreStrategy)
                .createdAt(Instant.now())
                .build();
    }

    public void update(String nameKo, String nameEn) {
        this.nameKo = nameKo;
        this.nameEn = nameEn;
    }
}
