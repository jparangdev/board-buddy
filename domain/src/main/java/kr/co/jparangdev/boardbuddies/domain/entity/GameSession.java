package kr.co.jparangdev.boardbuddies.domain.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameSession {
    private Long id;
    private Long boardGameId;
    private Long hostUserId;
    private int maxPlayers;
    private LocalDateTime scheduledDate;
    private String location;
    private String description;
    private Set<Long> participants = new HashSet<>();

    @Builder
    public GameSession(Long id, Long boardGameId, Long hostUserId, int maxPlayers,
                       LocalDateTime scheduledDate, String location, String description, Set<Long> participants) {
        this.id = id;
        this.boardGameId = boardGameId;
        this.hostUserId = hostUserId;
        this.maxPlayers = maxPlayers;
        this.scheduledDate = scheduledDate;
        this.location = location;
        this.description = description;
        if (participants != null) this.participants = participants;
        // host auto-join
        if (hostUserId != null) this.participants.add(hostUserId);
    }

    public void update(int maxPlayers, LocalDateTime scheduledDate, String location, String description) {
        this.maxPlayers = maxPlayers;
        this.scheduledDate = scheduledDate;
        this.location = location;
        this.description = description;
    }

    public void join(Long userId) {
        if (participants.size() >= maxPlayers) {
            throw new IllegalStateException("Session is full");
        }
        participants.add(userId);
    }

    public void leave(Long userId) {
        if (userId.equals(hostUserId)) {
            throw new IllegalStateException("Host cannot leave the session");
        }
        participants.remove(userId);
    }
}
