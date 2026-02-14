package kr.co.jparangdev.boardbuddy.application.game.usecase;

import java.util.List;

import kr.co.jparangdev.boardbuddy.domain.game.CustomGame;

public interface CustomGameQueryUseCase {
    List<CustomGame> getCustomGamesByGroup(Long groupId);
    CustomGame getCustomGameDetail(Long customGameId);
}
