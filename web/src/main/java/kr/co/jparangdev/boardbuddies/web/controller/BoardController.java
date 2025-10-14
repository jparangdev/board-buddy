package kr.co.jparangdev.boardbuddies.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddies.application.usecases.BoardManagementUseCase;
import kr.co.jparangdev.boardbuddies.domain.entity.BoardGame;
import kr.co.jparangdev.boardbuddies.web.dto.BoardDto;
import kr.co.jparangdev.boardbuddies.web.mapper.BoardDtoMapper;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/boards", produces = "application/json")
@RequiredArgsConstructor
public class BoardController {

    private final BoardManagementUseCase useCase;
    private final BoardDtoMapper mapper;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<BoardDto.Response> create(@Valid @RequestBody BoardDto.CreateRequest req) {
        var created = useCase.create(mapper.toDomain(req));
        return new ResponseEntity<>(mapper.toResponse(created), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDto.Response> getById(@PathVariable Long id) {
        return useCase.getById(id)
                .map(b -> ResponseEntity.ok(mapper.toResponse(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<BoardDto.Response>> getAll() {
        var list = useCase.getAll().stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<BoardDto.Response> update(@PathVariable Long id, @Valid @RequestBody BoardDto.UpdateRequest req) {
        var updated = useCase.update(id, mapper.toDomain(req));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<BoardDto.Response>> searchByCategory(@RequestParam("category") BoardGame.Category category) {
        var list = useCase.searchByCategory(category).stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(list);
    }
}
