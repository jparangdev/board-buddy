package kr.co.jparangdev.boardbuddies.dal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_games")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardGameJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private int minPlayers;

    @Column(nullable = false)
    private int maxPlayers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    public BoardGameJpaEntity(Long id, String name, String description, int minPlayers, int maxPlayers, Category category) {
        this.id = id;
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
