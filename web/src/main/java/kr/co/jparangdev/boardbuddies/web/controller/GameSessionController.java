package kr.co.jparangdev.boardbuddies.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddies.application.usecases.GameSessionUseCase;
import kr.co.jparangdev.boardbuddies.web.dto.GameSessionDto;
import kr.co.jparangdev.boardbuddies.web.mapper.GameSessionDtoMapper;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/game-sessions", produces = "application/json")
@RequiredArgsConstructor
public class GameSessionController {

    private final GameSessionUseCase useCase;
    private final GameSessionDtoMapper mapper;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<GameSessionDto.Response> create(@Valid @RequestBody GameSessionDto.CreateRequest req) {
        var created = useCase.create(mapper.toDomain(req));
        return new ResponseEntity<>(mapper.toResponse(created), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameSessionDto.Response> getById(@PathVariable Long id) {
        return useCase.getById(id)
                .map(s -> ResponseEntity.ok(mapper.toResponse(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<GameSessionDto.Response>> getAll() {
        var list = useCase.getAll().stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<GameSessionDto.Response> update(@PathVariable Long id, @Valid @RequestBody GameSessionDto.UpdateRequest req) {
        var updated = useCase.update(id, mapper.toDomain(req));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/join", consumes = "application/json")
    public ResponseEntity<GameSessionDto.Response> join(@PathVariable Long id, @Valid @RequestBody GameSessionDto.JoinRequest req) {
        var updated = useCase.join(id, req.getUserId());
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}/participants/{userId}")
    public ResponseEntity<GameSessionDto.Response> leave(@PathVariable Long id, @PathVariable Long userId) {
        var updated = useCase.leave(id, userId);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @GetMapping("/search")
    public ResponseEntity<List<GameSessionDto.Response>> search(@RequestParam("location") String location) {
        var list = useCase.searchByLocation(location).stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(list);
    }
}
