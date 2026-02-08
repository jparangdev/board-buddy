package kr.co.jparangdev.boardbuddy.api.game.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameSessionDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "Game ID is required")
        private Long gameId;

        @NotNull(message = "Played at is required")
        private LocalDateTime playedAt;

        @NotEmpty(message = "At least one result is required")
        @Valid
        private List<ResultInput> results;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResultInput {
        @NotNull(message = "User ID is required")
        private Long userId;

        private Integer score;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private Long groupId;
        private Long gameId;
        private String gameName;
        private LocalDateTime playedAt;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class DetailResponse {
        private Long id;
        private Long groupId;
        private Long gameId;
        private String gameName;
        private LocalDateTime playedAt;
        private LocalDateTime createdAt;
        private List<ResultResponse> results;
    }

    @Getter
    @Builder
    public static class ResultResponse {
        private Long userId;
        private String nickname;
        private String userTag;
        private Integer score;
        private int rank;
    }

    @Getter
    @Builder
    public static class SessionListResponse {
        private List<Response> sessions;
    }
}
