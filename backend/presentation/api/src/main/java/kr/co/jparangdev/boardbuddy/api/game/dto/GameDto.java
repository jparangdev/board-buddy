package kr.co.jparangdev.boardbuddy.api.game.dto;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Game name is required")
        @Size(max = 100, message = "Game name must not exceed 100 characters")
        private String name;

        @Min(value = 1, message = "Minimum players must be at least 1")
        private int minPlayers;

        @Min(value = 1, message = "Maximum players must be at least 1")
        private int maxPlayers;

        @NotNull(message = "Score strategy is required")
        private String scoreStrategy;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String nameKo;
        private String nameEn;
        private int minPlayers;
        private int maxPlayers;
        private String scoreStrategy;
        private Instant createdAt;
    }

    @Getter
    @Builder
    public static class GameListResponse {
        private List<Response> games;
    }
}
