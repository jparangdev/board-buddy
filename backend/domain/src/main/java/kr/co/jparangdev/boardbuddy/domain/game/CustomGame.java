package kr.co.jparangdev.boardbuddy.domain.game;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomGame {
    private Long id;
    private Long groupId;
    private String name;
    private String nameKo;
    private String nameEn;
    private int minPlayers;
    private int maxPlayers;
    private ScoreStrategy scoreStrategy;
    private LocalDateTime createdAt;

    @Builder
    public CustomGame(Long id, Long groupId, String name, String nameKo, String nameEn,
                      int minPlayers, int maxPlayers,
                      ScoreStrategy scoreStrategy, LocalDateTime createdAt) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.scoreStrategy = scoreStrategy;
        this.createdAt = createdAt;
    }

    public static CustomGame create(Long groupId, String name, String nameKo, String nameEn,
                                     int minPlayers, int maxPlayers,
                                     ScoreStrategy scoreStrategy) {
        return CustomGame.builder()
                .groupId(groupId)
                .name(name)
                .nameKo(nameKo)
                .nameEn(nameEn)
                .minPlayers(minPlayers)
                .maxPlayers(maxPlayers)
                .scoreStrategy(scoreStrategy)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
