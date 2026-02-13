package kr.co.jparangdev.boardbuddy.api.game;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddy.api.game.dto.GameDto;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@Tag(name = "Game", description = "Game type management API")
public class GameController {

    private final GameQueryUseCase gameQueryUseCase;
    private final GameCommandUseCase gameCommandUseCase;
    private final GameDtoMapper mapper;

    @GetMapping
    @Operation(summary = "Get game list", description = "Retrieve all available game types")
    public ResponseEntity<GameDto.GameListResponse> getGameList() {
        return ResponseEntity.ok(mapper.toGameListResponse(gameQueryUseCase.getGameList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get game detail", description = "Retrieve game type details by ID")
    public ResponseEntity<GameDto.Response> getGameDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(mapper.toGameResponse(gameQueryUseCase.getGameDetail(id)));
    }

    @PostMapping
    @Operation(summary = "Create game", description = "Register a new game type")
    public ResponseEntity<GameDto.Response> createGame(@Valid @RequestBody GameDto.CreateRequest request) {
        Game game = gameCommandUseCase.createGame(
                request.getName(),
                request.getMinPlayers(),
                request.getMaxPlayers(),
                ScoreStrategy.valueOf(request.getScoreStrategy())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toGameResponse(game));
    }
}
