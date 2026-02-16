package kr.co.jparangdev.boardbuddy.application.game.usecase;

import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;

public interface GameCommandUseCase {
    Game createGame(String name, int minPlayers, int maxPlayers, ScoreStrategy scoreStrategy);
    Game createGame(String name, String nameKo, String nameEn, int minPlayers, int maxPlayers, ScoreStrategy scoreStrategy);
    Game updateGame(Long id, String nameKo, String nameEn);
}
