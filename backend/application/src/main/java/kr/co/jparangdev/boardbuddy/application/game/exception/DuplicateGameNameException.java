package kr.co.jparangdev.boardbuddy.application.game.exception;

public class DuplicateGameNameException extends RuntimeException {
    public DuplicateGameNameException(String name) {
        super("Game with name already exists: " + name);
    }
}
