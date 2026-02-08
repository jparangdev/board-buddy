package kr.co.jparangdev.boardbuddy.domain.game.exception;

public class GameSessionNotFoundException extends RuntimeException {
    public GameSessionNotFoundException(Long sessionId) {
        super("Game session not found: " + sessionId);
    }
}
