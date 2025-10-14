package kr.co.jparangdev.boardbuddies.web.dto;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class GameSessionDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateRequest {
        @NotNull
        private Long boardGameId;
        @NotNull
        private Long hostUserId;
        @Min(1)
        private int maxPlayers;
        @NotNull
        private LocalDateTime scheduledDate;
        private String location;
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateRequest {
        @Min(1)
        private int maxPlayers;
        @NotNull
        private LocalDateTime scheduledDate;
        private String location;
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class JoinRequest {
        @NotNull
        private Long userId;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private Long boardGameId;
        private Long hostUserId;
        private int maxPlayers;
        private LocalDateTime scheduledDate;
        private String location;
        private String description;
        private Set<Long> participants;
    }
}
