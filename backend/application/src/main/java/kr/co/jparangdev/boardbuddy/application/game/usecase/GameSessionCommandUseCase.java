package kr.co.jparangdev.boardbuddy.application.game.usecase;

import java.time.Instant;
import java.util.List;

import kr.co.jparangdev.boardbuddy.domain.game.GameSession;
import kr.co.jparangdev.boardbuddy.domain.game.SessionConfig;

public interface GameSessionCommandUseCase {

    record ResultInput(Long userId, Integer score, Boolean won) {}

    GameSession createSession(Long groupId, Long gameId, Instant playedAt, List<ResultInput> results, SessionConfig config);

    GameSession createSessionWithCustomGame(Long groupId, Long customGameId, Instant playedAt, List<ResultInput> results, SessionConfig config);
}
