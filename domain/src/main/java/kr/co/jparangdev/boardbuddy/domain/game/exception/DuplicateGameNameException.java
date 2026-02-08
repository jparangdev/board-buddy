package kr.co.jparangdev.boardbuddy.domain.game.exception;

public class DuplicateGameNameException extends RuntimeException {
    public DuplicateGameNameException(String name) {
        super("Game with name already exists: " + name);
    }
}
