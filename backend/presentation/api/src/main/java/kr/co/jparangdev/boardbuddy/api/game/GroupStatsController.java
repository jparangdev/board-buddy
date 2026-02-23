package kr.co.jparangdev.boardbuddy.api.game;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.jparangdev.boardbuddy.api.game.dto.GroupStatsDto;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GroupStatsQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.game.GroupStats;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/stats")
@RequiredArgsConstructor
@Tag(name = "GroupStats", description = "Group statistics API")
public class GroupStatsController {

    private final GroupStatsQueryUseCase groupStatsQueryUseCase;
    private final GameDtoMapper mapper;

    @GetMapping
    @Operation(summary = "Get group stats", description = "Get aggregated game statistics for a group")
    public ResponseEntity<GroupStatsDto.Response> getGroupStats(
            @PathVariable("groupId") Long groupId) {

        GroupStats stats = groupStatsQueryUseCase.getGroupStats(groupId);
        return ResponseEntity.ok(mapper.toGroupStatsResponse(stats));
    }
}
