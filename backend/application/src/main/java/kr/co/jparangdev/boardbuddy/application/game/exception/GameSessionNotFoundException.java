package kr.co.jparangdev.boardbuddy.application.game.exception;

public class GameSessionNotFoundException extends RuntimeException {
    public GameSessionNotFoundException(Long sessionId) {
        super("Game session not found: " + sessionId);
    }
}
