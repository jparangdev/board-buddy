package kr.co.jparangdev.boardbuddy.domain.game;

import java.util.List;

public record GroupStats(
        long totalSessions,
        long totalParticipations,
        List<PlayerStat> mostActivePlayers,
        List<PlayerStat> mostWins,
        List<WinRateStat> winRateRanking,
        List<ScoreStat> totalScoreRanking,
        List<GamePlayStat> mostPlayedGames
) {

    public record PlayerStat(
            Long userId,
            String nickname,
            String userTag,
            long count
    ) {}

    public record WinRateStat(
            Long userId,
            String nickname,
            String userTag,
            double winRate,
            long totalGames,
            long wins
    ) {}

    public record ScoreStat(
            Long userId,
            String nickname,
            String userTag,
            long totalScore
    ) {}

    public record GamePlayStat(
            String gameName,
            long playCount
    ) {}
}
