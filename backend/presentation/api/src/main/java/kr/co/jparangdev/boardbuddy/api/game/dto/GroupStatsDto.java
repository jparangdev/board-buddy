package kr.co.jparangdev.boardbuddy.api.game.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class GroupStatsDto {

    @Getter
    @Builder
    public static class ActivePlayerEntry {
        private Long userId;
        private String nickname;
        private String userTag;
        private long sessionCount;
    }

    @Getter
    @Builder
    public static class WinPlayerEntry {
        private Long userId;
        private String nickname;
        private String userTag;
        private long winCount;
    }

    @Getter
    @Builder
    public static class WinRateEntry {
        private Long userId;
        private String nickname;
        private String userTag;
        private double winRate;
        private long totalGames;
        private long wins;
    }

    @Getter
    @Builder
    public static class GameStatEntry {
        private String gameName;
        private long playCount;
    }

    @Getter
    @Builder
    public static class Response {
        private long totalSessions;
        private long totalParticipations;
        private List<ActivePlayerEntry> mostActivePlayers;
        private List<WinPlayerEntry> mostWins;
        private List<WinRateEntry> winRateRanking;
        private List<GameStatEntry> mostPlayedGames;
    }
}
