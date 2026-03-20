package kr.co.jparangdev.boardbuddy.api.game;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddy.api.game.dto.GameSessionDto;
import kr.co.jparangdev.boardbuddy.application.game.usecase.CustomGameQueryUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameQueryUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameSessionCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameSessionQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.game.CustomGame;
import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.GameResult;
import kr.co.jparangdev.boardbuddy.domain.game.GameSession;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import kr.co.jparangdev.boardbuddy.domain.game.SessionConfig;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/sessions")
@RequiredArgsConstructor
@Tag(name = "GameSession", description = "Game session management API")
public class GameSessionController {

    private final GameSessionQueryUseCase gameSessionQueryUseCase;
    private final GameSessionCommandUseCase gameSessionCommandUseCase;
    private final GameQueryUseCase gameQueryUseCase;
    private final CustomGameQueryUseCase customGameQueryUseCase;
    private final UserRepository userRepository;
    private final GameDtoMapper mapper;

    @PostMapping
    @Operation(summary = "Create session", description = "Create a new game session with results")
    public ResponseEntity<GameSessionDto.Response> createSession(
            @PathVariable("groupId") Long groupId,
            @Valid @RequestBody GameSessionDto.CreateRequest request) {

        List<GameSessionCommandUseCase.ResultInput> results = request.getResults().stream()
                .map(r -> new GameSessionCommandUseCase.ResultInput(r.getUserId(), r.getScore(), r.getWon()))
                .toList();

        ScoreStrategy scoreStrategy = ScoreStrategy.valueOf(request.getScoreStrategy());
        SessionConfig config = new SessionConfig(scoreStrategy, request.getWinnerCount(), request.getWinPoints(), request.getLosePoints());

        GameSession session;
        String gameName;

        if (request.getCustomGameId() != null) {
            session = gameSessionCommandUseCase.createSessionWithCustomGame(
                    groupId, request.getCustomGameId(), request.getPlayedAt(), results, config);
            CustomGame customGame = customGameQueryUseCase.getCustomGameDetail(request.getCustomGameId());
            gameName = customGame.getName();
        } else {
            session = gameSessionCommandUseCase.createSession(
                    groupId, request.getGameId(), request.getPlayedAt(), results, config);
            Game game = gameQueryUseCase.getGameDetail(request.getGameId());
            gameName = game.getName();
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toSessionResponse(session, gameName));
    }

    @GetMapping
    @Operation(summary = "Get sessions", description = "Get all game sessions for a group")
    public ResponseEntity<GameSessionDto.SessionListResponse> getSessionsByGroup(
            @PathVariable("groupId") Long groupId) {

        List<GameSession> sessions = gameSessionQueryUseCase.getSessionsByGroup(groupId);

        Map<Long, String> gameNames = sessions.stream()
                .filter(s -> s.getGameId() != null)
                .map(GameSession::getGameId)
                .distinct()
                .collect(Collectors.toMap(
                        gameId -> gameId,
                        gameId -> gameQueryUseCase.getGameDetail(gameId).getName()
                ));

        Map<Long, String> customGameNames = sessions.stream()
                .filter(s -> s.getCustomGameId() != null)
                .map(GameSession::getCustomGameId)
                .distinct()
                .collect(Collectors.toMap(
                        customGameId -> customGameId,
                        customGameId -> customGameQueryUseCase.getCustomGameDetail(customGameId).getName()
                ));

        return ResponseEntity.ok(mapper.toSessionListResponse(sessions, gameNames, customGameNames));
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get session detail", description = "Get game session details with results")
    public ResponseEntity<GameSessionDto.DetailResponse> getSessionDetail(
            @PathVariable("groupId") Long groupId,
            @PathVariable("sessionId") Long sessionId) {

        GameSession session = gameSessionQueryUseCase.getSessionDetail(sessionId);
        List<GameResult> results = gameSessionQueryUseCase.getSessionResults(sessionId);

        String gameName;
        if (session.getCustomGameId() != null) {
            CustomGame customGame = customGameQueryUseCase.getCustomGameDetail(session.getCustomGameId());
            gameName = customGame.getName();
        } else {
            Game game = gameQueryUseCase.getGameDetail(session.getGameId());
            gameName = game.getName();
        }
        String scoreStrategy = session.getScoreStrategy() != null
                ? session.getScoreStrategy().name()
                : ScoreStrategy.HIGH_WIN.name();

        List<Long> userIds = results.stream().map(GameResult::getUserId).toList();
        List<User> users = userIds.stream()
                .map(id -> userRepository.findById(id).orElse(null))
                .filter(u -> u != null)
                .toList();

        return ResponseEntity.ok(mapper.toSessionDetailResponse(session, gameName, scoreStrategy, results, users));
    }
}
