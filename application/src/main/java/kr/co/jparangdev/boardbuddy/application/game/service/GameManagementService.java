package kr.co.jparangdev.boardbuddy.application.game.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.game.exception.DuplicateGameNameException;
import kr.co.jparangdev.boardbuddy.application.game.exception.GameNotFoundException;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import kr.co.jparangdev.boardbuddy.domain.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameManagementService implements GameQueryUseCase, GameCommandUseCase {

    private final GameRepository gameRepository;

    @Override
    public List<Game> getGameList() {
        return gameRepository.findAll();
    }

    @Override
    public Game getGameDetail(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }

    @Override
    @Transactional
    public Game createGame(String name, int minPlayers, int maxPlayers, ScoreStrategy scoreStrategy) {
        if (gameRepository.existsByName(name)) {
            throw new DuplicateGameNameException(name);
        }
        Game game = Game.create(name, minPlayers, maxPlayers, scoreStrategy);
        return gameRepository.save(game);
    }
}
