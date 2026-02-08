package kr.co.jparangdev.boardbuddy.application.game.usecase;

import java.util.List;

import kr.co.jparangdev.boardbuddy.domain.game.GameResult;
import kr.co.jparangdev.boardbuddy.domain.game.GameSession;

public interface GameSessionQueryUseCase {
    List<GameSession> getSessionsByGroup(Long groupId);
    GameSession getSessionDetail(Long sessionId);
    List<GameResult> getSessionResults(Long sessionId);
}
