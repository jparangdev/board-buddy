package kr.co.jparangdev.boardbuddy.application.game.exception;

public class CustomGameNotFoundException extends RuntimeException {
    public CustomGameNotFoundException(Long customGameId) {
        super("Custom game not found: " + customGameId);
    }
}
