package kr.co.jparangdev.boardbuddy.persistence.game;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "custom_games",
       uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "name"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomGameJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(nullable = false, length = 100)
    private String name;

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

    public CustomGameJpaEntity(Long id, Long groupId, String name, int minPlayers, int maxPlayers,
                                String scoreStrategy, LocalDateTime createdAt) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.scoreStrategy = scoreStrategy;
        this.createdAt = createdAt;
    }
}
