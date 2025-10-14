package kr.co.jparangdev.boardbuddies.web.dto;

import jakarta.validation.constraints.*;
import kr.co.jparangdev.boardbuddies.domain.entity.BoardGame;
import lombok.*;

public class BoardDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank
        private String name;
        private String description;
        @Min(1)
        private int minPlayers;
        @Min(1) @Max(20)
        private int maxPlayers;
        @NotNull
        private BoardGame.Category category;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateRequest {
        @NotBlank
        private String name;
        private String description;
        @Min(1)
        private int minPlayers;
        @Min(1) @Max(20)
        private int maxPlayers;
        @NotNull
        private BoardGame.Category category;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private int minPlayers;
        private int maxPlayers;
        private BoardGame.Category category;
    }
}
