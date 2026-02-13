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
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameQueryUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameSessionCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameSessionQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.GameResult;
import kr.co.jparangdev.boardbuddy.domain.game.GameSession;
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
    private final UserRepository userRepository;
    private final GameDtoMapper mapper;

    @PostMapping
    @Operation(summary = "Create session", description = "Create a new game session with results")
    public ResponseEntity<GameSessionDto.Response> createSession(
            @PathVariable("groupId") Long groupId,
            @Valid @RequestBody GameSessionDto.CreateRequest request) {

        List<GameSessionCommandUseCase.ResultInput> results = request.getResults().stream()
                .map(r -> new GameSessionCommandUseCase.ResultInput(r.getUserId(), r.getScore()))
                .toList();

        GameSession session = gameSessionCommandUseCase.createSession(
                groupId, request.getGameId(), request.getPlayedAt(), results);

        Game game = gameQueryUseCase.getGameDetail(request.getGameId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toSessionResponse(session, game.getName()));
    }

    @GetMapping
    @Operation(summary = "Get sessions", description = "Get all game sessions for a group")
    public ResponseEntity<GameSessionDto.SessionListResponse> getSessionsByGroup(
            @PathVariable("groupId") Long groupId) {

        List<GameSession> sessions = gameSessionQueryUseCase.getSessionsByGroup(groupId);

        Map<Long, String> gameNames = sessions.stream()
                .map(GameSession::getGameId)
                .distinct()
                .collect(Collectors.toMap(
                        gameId -> gameId,
                        gameId -> gameQueryUseCase.getGameDetail(gameId).getName()
                ));

        return ResponseEntity.ok(mapper.toSessionListResponse(sessions, gameNames));
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get session detail", description = "Get game session details with results")
    public ResponseEntity<GameSessionDto.DetailResponse> getSessionDetail(
            @PathVariable("groupId") Long groupId,
            @PathVariable("sessionId") Long sessionId) {

        GameSession session = gameSessionQueryUseCase.getSessionDetail(sessionId);
        List<GameResult> results = gameSessionQueryUseCase.getSessionResults(sessionId);
        Game game = gameQueryUseCase.getGameDetail(session.getGameId());

        List<Long> userIds = results.stream().map(GameResult::getUserId).toList();
        List<User> users = userIds.stream()
                .map(id -> userRepository.findById(id).orElse(null))
                .filter(u -> u != null)
                .toList();

        return ResponseEntity.ok(mapper.toSessionDetailResponse(session, game.getName(), results, users));
    }
}
