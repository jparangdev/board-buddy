package kr.co.jparangdev.boardbuddy.application.game.usecase;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.jparangdev.boardbuddy.domain.game.GameSession;

public interface GameSessionCommandUseCase {

    record ResultInput(Long userId, Integer score) {}

    GameSession createSession(Long groupId, Long gameId, LocalDateTime playedAt, List<ResultInput> results);
}
