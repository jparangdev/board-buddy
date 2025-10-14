package kr.co.jparangdev.boardbuddies.domain.entity;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardGame {
    private Long id;
    private String name;
    private String description;
    private int minPlayers;
    private int maxPlayers;
    private Category category;

    @Builder
    public BoardGame(Long id, String name, String description, int minPlayers, int maxPlayers, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.category = category;
    }

    public void update(String name, String description, int minPlayers, int maxPlayers, Category category) {
        this.name = name;
        this.description = description;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.category = category;
    }

    public enum Category {
        STRATEGY, PARTY, FAMILY, COOPERATIVE, CARD, DICE, ABSTRACT, THEME
    }
}
