package kr.co.jparangdev.boardbuddy.application.game.usecase;

import kr.co.jparangdev.boardbuddy.domain.game.CustomGame;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;

public interface CustomGameCommandUseCase {
    CustomGame createCustomGame(Long groupId, String name, int minPlayers, int maxPlayers, ScoreStrategy scoreStrategy);
}
