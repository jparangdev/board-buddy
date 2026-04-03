package kr.co.jparangdev.boardbuddy.api.game;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.api.game.dto.*;
import kr.co.jparangdev.boardbuddy.domain.game.*;
import kr.co.jparangdev.boardbuddy.domain.user.User;

@Component
public class GameDtoMapper {

    public GameDto.Response toGameResponse(Game game) {
        return GameDto.Response.builder()
            .id(game.getId())
            .name(game.getName())
            .nameKo(game.getNameKo())
            .nameEn(game.getNameEn())
            .minPlayers(game.getMinPlayers())
            .maxPlayers(game.getMaxPlayers())
            .scoreStrategy(game.getScoreStrategy().name())
            .createdAt(game.getCreatedAt())
            .build();
    }

    public GameDto.GameListResponse toGameListResponse(List<Game> games) {
        List<GameDto.Response> responses = games.stream()
            .map(this::toGameResponse)
            .toList();
        return GameDto.GameListResponse.builder()
            .games(responses)
            .build();
    }

    public GameSessionDto.Response toSessionResponse(GameSession session, String gameName) {
        return GameSessionDto.Response.builder()
            .id(session.getId())
            .groupId(session.getGroupId())
            .gameId(session.getGameId())
            .customGameId(session.getCustomGameId())
            .gameName(gameName)
            .playedAt(session.getPlayedAt())
            .createdAt(session.getCreatedAt())
            .build();
    }

    public GameSessionDto.DetailResponse toSessionDetailResponse(GameSession session, String gameName,
                                                                  String scoreStrategy,
                                                                  List<GameResult> results, List<User> users) {
        List<GameSessionDto.ResultResponse> resultResponses = results.stream()
            .map(result -> {
                User user = users.stream()
                    .filter(u -> u.getId().equals(result.getUserId()))
                    .findFirst()
                    .orElse(null);
                return GameSessionDto.ResultResponse.builder()
                    .userId(result.getUserId())
                    .nickname(user != null ? user.getNickname() : null)
                    .userTag(user != null ? user.getUserTag() : null)
                    .score(result.getScore())
                    .rank(result.getRank())
                    .teamId(result.getTeamId())
                    .build();
            })
            .toList();

        List<Integer> rankPoints = null;
        if (session.getRankPoints() != null && !session.getRankPoints().isBlank()) {
            rankPoints = Arrays.stream(session.getRankPoints().split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        return GameSessionDto.DetailResponse.builder()
            .id(session.getId())
            .groupId(session.getGroupId())
            .gameId(session.getGameId())
            .customGameId(session.getCustomGameId())
            .gameName(gameName)
            .scoreStrategy(scoreStrategy)
            .playedAt(session.getPlayedAt())
            .createdAt(session.getCreatedAt())
            .results(resultResponses)
            .rankPoints(rankPoints)
            .build();
    }

    public GameSessionDto.SessionListResponse toSessionListResponse(List<GameSession> sessions,
                                                                     Map<Long, String> gameNames,
                                                                     Map<Long, String> customGameNames) {
        List<GameSessionDto.Response> responses = sessions.stream()
            .map(s -> {
                String name;
                if (s.getCustomGameId() != null) {
                    name = customGameNames.getOrDefault(s.getCustomGameId(), "");
                } else {
                    name = gameNames.getOrDefault(s.getGameId(), "");
                }
                return toSessionResponse(s, name);
            })
            .toList();
        return GameSessionDto.SessionListResponse.builder()
            .sessions(responses)
            .build();
    }

    public CustomGameDto.Response toCustomGameResponse(CustomGame customGame) {
        return CustomGameDto.Response.builder()
            .id(customGame.getId())
            .groupId(customGame.getGroupId())
            .name(customGame.getName())
            .nameKo(customGame.getNameKo())
            .nameEn(customGame.getNameEn())
            .minPlayers(customGame.getMinPlayers())
            .maxPlayers(customGame.getMaxPlayers())
            .scoreStrategy(customGame.getScoreStrategy().name())
            .createdAt(customGame.getCreatedAt())
            .build();
    }

    public CustomGameDto.ListResponse toCustomGameListResponse(List<CustomGame> customGames) {
        List<CustomGameDto.Response> responses = customGames.stream()
            .map(this::toCustomGameResponse)
            .toList();
        return CustomGameDto.ListResponse.builder()
            .customGames(responses)
            .build();
    }

    public GroupStatsDto.Response toGroupStatsResponse(GroupStats stats) {
        List<GroupStatsDto.ActivePlayerEntry> mostActivePlayers = stats.mostActivePlayers().stream()
            .map(p -> GroupStatsDto.ActivePlayerEntry.builder()
                .userId(p.userId())
                .nickname(p.nickname())
                .userTag(p.userTag())
                .sessionCount(p.count())
                .build())
            .toList();

        List<GroupStatsDto.WinPlayerEntry> mostWins = stats.mostWins().stream()
            .map(p -> GroupStatsDto.WinPlayerEntry.builder()
                .userId(p.userId())
                .nickname(p.nickname())
                .userTag(p.userTag())
                .winCount(p.count())
                .build())
            .toList();

        List<GroupStatsDto.WinRateEntry> winRateRanking = stats.winRateRanking().stream()
            .map(p -> GroupStatsDto.WinRateEntry.builder()
                .userId(p.userId())
                .nickname(p.nickname())
                .userTag(p.userTag())
                .winRate(p.winRate())
                .totalGames(p.totalGames())
                .wins(p.wins())
                .build())
            .toList();

        List<GroupStatsDto.ScorePlayerEntry> totalScoreRanking = stats.totalScoreRanking().stream()
            .map(p -> GroupStatsDto.ScorePlayerEntry.builder()
                .userId(p.userId())
                .nickname(p.nickname())
                .userTag(p.userTag())
                .totalScore(p.totalScore())
                .build())
            .toList();

        List<GroupStatsDto.GameStatEntry> mostPlayedGames = stats.mostPlayedGames().stream()
            .map(g -> GroupStatsDto.GameStatEntry.builder()
                .gameName(g.gameName())
                .playCount(g.playCount())
                .build())
            .toList();

        return GroupStatsDto.Response.builder()
            .totalSessions(stats.totalSessions())
            .totalParticipations(stats.totalParticipations())
            .mostActivePlayers(mostActivePlayers)
            .mostWins(mostWins)
            .winRateRanking(winRateRanking)
            .totalScoreRanking(totalScoreRanking)
            .mostPlayedGames(mostPlayedGames)
            .build();
    }
}
