package kr.co.jparangdev.boardbuddy.domain.game.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(Long gameId) {
        super("Game not found: " + gameId);
    }
}
