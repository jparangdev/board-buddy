package kr.co.jparangdev.boardbuddy.api.game;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.api.game.dto.CustomGameDto;
import kr.co.jparangdev.boardbuddy.api.game.dto.GameDto;
import kr.co.jparangdev.boardbuddy.api.game.dto.GameSessionDto;
import kr.co.jparangdev.boardbuddy.domain.game.CustomGame;
import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.GameResult;
import kr.co.jparangdev.boardbuddy.domain.game.GameSession;
import kr.co.jparangdev.boardbuddy.domain.user.User;

@Component
public class GameDtoMapper {

    public GameDto.Response toGameResponse(Game game) {
        return GameDto.Response.builder()
            .id(game.getId())
            .name(game.getName())
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
                    .build();
            })
            .toList();

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
}
