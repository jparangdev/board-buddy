package kr.co.jparangdev.boardbuddies.web.mapper;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddies.domain.entity.GameSession;
import kr.co.jparangdev.boardbuddies.web.dto.GameSessionDto;

@Component
public class GameSessionDtoMapper {

    public GameSession toDomain(GameSessionDto.CreateRequest req) {
        if (req == null) return null;
        return GameSession.builder()
                .boardGameId(req.getBoardGameId())
                .hostUserId(req.getHostUserId())
                .maxPlayers(req.getMaxPlayers())
                .scheduledDate(req.getScheduledDate())
                .location(req.getLocation())
                .description(req.getDescription())
                .build();
    }

    public GameSession toDomain(GameSessionDto.UpdateRequest req) {
        if (req == null) return null;
        return GameSession.builder()
                .maxPlayers(req.getMaxPlayers())
                .scheduledDate(req.getScheduledDate())
                .location(req.getLocation())
                .description(req.getDescription())
                .build();
    }

    public GameSessionDto.Response toResponse(GameSession session) {
        if (session == null) return null;
        return GameSessionDto.Response.builder()
                .id(session.getId())
                .boardGameId(session.getBoardGameId())
                .hostUserId(session.getHostUserId())
                .maxPlayers(session.getMaxPlayers())
                .scheduledDate(session.getScheduledDate())
                .location(session.getLocation())
                .description(session.getDescription())
                .participants(session.getParticipants())
                .build();
    }
}
