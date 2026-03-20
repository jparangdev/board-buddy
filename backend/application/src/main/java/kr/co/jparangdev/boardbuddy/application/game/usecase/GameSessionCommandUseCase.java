package kr.co.jparangdev.boardbuddy.application.game.usecase;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.jparangdev.boardbuddy.domain.game.GameSession;
import kr.co.jparangdev.boardbuddy.domain.game.SessionConfig;

public interface GameSessionCommandUseCase {

    record ResultInput(Long userId, Integer score, Boolean won) {}

    GameSession createSession(Long groupId, Long gameId, LocalDateTime playedAt, List<ResultInput> results, SessionConfig config);

    GameSession createSessionWithCustomGame(Long groupId, Long customGameId, LocalDateTime playedAt, List<ResultInput> results, SessionConfig config);
}
