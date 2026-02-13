package kr.co.jparangdev.boardbuddy.application.game.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(Long gameId) {
        super("Game not found: " + gameId);
    }
}
