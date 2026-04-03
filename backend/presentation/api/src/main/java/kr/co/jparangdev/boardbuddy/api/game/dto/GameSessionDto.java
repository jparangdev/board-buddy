package kr.co.jparangdev.boardbuddy.api.game.dto;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameSessionDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateRequest {
        private Long gameId;

        private Long customGameId;

        @NotNull(message = "Played at is required")
        private Instant playedAt;

        @NotEmpty(message = "At least one result is required")
        @Valid
        private List<ResultInput> results;

        /** Scoring strategy for this session. Default RANK_ONLY. */
        private String scoreStrategy = "RANK_ONLY";

        /** RANK_ONLY: how many top ranks count as a win. Default 1. */
        private int winnerCount = 1;

        /** WIN_LOSE / COOPERATIVE: points awarded to winners. Default 3. */
        private int winPoints = 3;

        /** WIN_LOSE / COOPERATIVE: points awarded to losers. Default 0. */
        private int losePoints = 0;

        /**
         * RANK_SCORE: points per rank position (index 0 = 1st place).
         * E.g. [10, 7, 5, 3] means 1st gets 10 pts, 2nd gets 7 pts, etc.
         */
        private List<Integer> rankPoints;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResultInput {
        @NotNull(message = "User ID is required")
        private Long userId;

        private Integer score;

        private Boolean won;

        /** Optional team number. Players sharing the same teamId form a team (requires 3+ players). */
        private Integer teamId;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private Long groupId;
        private Long gameId;
        private Long customGameId;
        private String gameName;
        private Instant playedAt;
        private Instant createdAt;
    }

    @Getter
    @Builder
    public static class DetailResponse {
        private Long id;
        private Long groupId;
        private Long gameId;
        private Long customGameId;
        private String gameName;
        private String scoreStrategy;
        private Instant playedAt;
        private Instant createdAt;
        private List<ResultResponse> results;
        /** RANK_SCORE only: points per rank position (index 0 = 1st place). */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<Integer> rankPoints;
    }

    @Getter
    @Builder
    public static class ResultResponse {
        private Long userId;
        private String nickname;
        private String userTag;
        private Integer score;
        private int rank;
        private Integer teamId;
    }

    @Getter
    @Builder
    public static class SessionListResponse {
        private List<Response> sessions;
    }
}
