package kr.co.jparangdev.boardbuddy.domain.game.exception;

public class CustomGameNotFoundException extends RuntimeException {
    public CustomGameNotFoundException(Long customGameId) {
        super("Custom game not found: " + customGameId);
    }
}
