package kr.co.jparangdev.boardbuddies.dal.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_sessions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameSessionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long boardGameId;

    @Column(nullable = false)
    private Long hostUserId;

    @Column(nullable = false)
    private int maxPlayers;

    @Column(nullable = false)
    private LocalDateTime scheduledDate;

    private String location;

    @Column(length = 2000)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "game_session_participants",
            joinColumns = @JoinColumn(name = "session_id")
    )
    @Column(name = "user_id")
    private Set<Long> participants = new HashSet<>();

    public GameSessionJpaEntity(Long id, Long boardGameId, Long hostUserId, int maxPlayers,
                                LocalDateTime scheduledDate, String location, String description, Set<Long> participants) {
        this.id = id;
        this.boardGameId = boardGameId;
        this.hostUserId = hostUserId;
        this.maxPlayers = maxPlayers;
        this.scheduledDate = scheduledDate;
        this.location = location;
        this.description = description;
        if (participants != null) this.participants = participants;
    }
}
