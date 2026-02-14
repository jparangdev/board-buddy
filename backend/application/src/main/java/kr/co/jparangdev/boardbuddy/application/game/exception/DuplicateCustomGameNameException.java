package kr.co.jparangdev.boardbuddy.application.game.exception;

public class DuplicateCustomGameNameException extends RuntimeException {
    public DuplicateCustomGameNameException(Long groupId, String name) {
        super("Custom game with name already exists in group " + groupId + ": " + name);
    }
}
