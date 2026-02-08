package kr.co.jparangdev.boardbuddy.application.game.usecase;

import java.util.List;

import kr.co.jparangdev.boardbuddy.domain.game.Game;

public interface GameQueryUseCase {
    List<Game> getGameList();
    Game getGameDetail(Long gameId);
}
