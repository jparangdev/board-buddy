package kr.co.jparangdev.boardbuddy.api.game;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddy.api.game.dto.CustomGameDto;
import kr.co.jparangdev.boardbuddy.application.game.usecase.CustomGameCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.CustomGameQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.game.CustomGame;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/custom-games")
@RequiredArgsConstructor
@Tag(name = "CustomGame", description = "Group custom game management API")
public class CustomGameController {

    private final CustomGameQueryUseCase customGameQueryUseCase;
    private final CustomGameCommandUseCase customGameCommandUseCase;
    private final GameDtoMapper mapper;

    @GetMapping
    @Operation(summary = "Get custom games", description = "Get all custom games for a group")
    public ResponseEntity<CustomGameDto.ListResponse> getCustomGames(
            @PathVariable("groupId") Long groupId) {
        return ResponseEntity.ok(
            mapper.toCustomGameListResponse(customGameQueryUseCase.getCustomGamesByGroup(groupId)));
    }

    @PostMapping
    @Operation(summary = "Create custom game", description = "Create a new custom game for a group")
    public ResponseEntity<CustomGameDto.Response> createCustomGame(
            @PathVariable("groupId") Long groupId,
            @Valid @RequestBody CustomGameDto.CreateRequest request) {
        CustomGame customGame = customGameCommandUseCase.createCustomGame(
                groupId,
                request.getName(),
                request.getMinPlayers(),
                request.getMaxPlayers(),
                ScoreStrategy.valueOf(request.getScoreStrategy())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCustomGameResponse(customGame));
    }
}
