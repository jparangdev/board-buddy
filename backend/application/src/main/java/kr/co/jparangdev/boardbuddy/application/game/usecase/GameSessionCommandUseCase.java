package kr.co.jparangdev.boardbuddy.application.game.usecase;

import java.time.Instant;
import java.util.List;

import kr.co.jparangdev.boardbuddy.domain.game.GameSession;
import kr.co.jparangdev.boardbuddy.domain.game.SessionConfig;

public interface GameSessionCommandUseCase {

    /**
     * @param teamId optional; players sharing the same teamId form a team within this session.
     *               Team mode is activated when at least one result has a non-null teamId.
     */
    record ResultInput(Long userId, Integer score, Boolean won, Integer teamId) {}

    GameSession createSession(Long groupId, Long gameId, Instant playedAt, List<ResultInput> results, SessionConfig config);

    GameSession createSessionWithCustomGame(Long groupId, Long customGameId, Instant playedAt, List<ResultInput> results, SessionConfig config);
}
