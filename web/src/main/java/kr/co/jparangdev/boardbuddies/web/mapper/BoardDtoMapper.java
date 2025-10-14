package kr.co.jparangdev.boardbuddies.web.mapper;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddies.domain.entity.BoardGame;
import kr.co.jparangdev.boardbuddies.web.dto.BoardDto;

@Component
public class BoardDtoMapper {

    public BoardGame toDomain(BoardDto.CreateRequest req) {
        if (req == null) return null;
        return BoardGame.builder()
                .name(req.getName())
                .description(req.getDescription())
                .minPlayers(req.getMinPlayers())
                .maxPlayers(req.getMaxPlayers())
                .category(req.getCategory())
                .build();
    }

    public BoardGame toDomain(BoardDto.UpdateRequest req) {
        if (req == null) return null;
        return BoardGame.builder()
                .name(req.getName())
                .description(req.getDescription())
                .minPlayers(req.getMinPlayers())
                .maxPlayers(req.getMaxPlayers())
                .category(req.getCategory())
                .build();
    }

    public BoardDto.Response toResponse(BoardGame board) {
        if (board == null) return null;
        return BoardDto.Response.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .minPlayers(board.getMinPlayers())
                .maxPlayers(board.getMaxPlayers())
                .category(board.getCategory())
                .build();
    }
}
