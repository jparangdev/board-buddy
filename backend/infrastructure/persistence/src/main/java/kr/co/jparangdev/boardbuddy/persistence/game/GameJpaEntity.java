package kr.co.jparangdev.boardbuddy.persistence.game;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "name_ko", length = 100)
    private String nameKo;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(name = "min_players", nullable = false)
    private int minPlayers;

    @Column(name = "max_players", nullable = false)
    private int maxPlayers;

    @Column(name = "score_strategy", nullable = false, length = 20)
    private String scoreStrategy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public GameJpaEntity(Long id, String name, String nameKo, String nameEn, int minPlayers, int maxPlayers,
                         String scoreStrategy, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.scoreStrategy = scoreStrategy;
        this.createdAt = createdAt;
    }
}
