package kr.co.jparangdev.boardbuddy.application.game.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.game.usecase.GameCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameQueryUseCase;
import kr.co.jparangdev.boardbuddy.application.shared.TxExecutor;
import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import kr.co.jparangdev.boardbuddy.domain.game.exception.DuplicateGameNameException;
import kr.co.jparangdev.boardbuddy.domain.game.exception.GameNotFoundException;
import kr.co.jparangdev.boardbuddy.domain.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameManagementService implements GameQueryUseCase, GameCommandUseCase {

    private final GameRepository gameRepository;
    private final TxExecutor transactionExecutor;

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
    public Game createGame(String name, int minPlayers, int maxPlayers, ScoreStrategy scoreStrategy) {
        return transactionExecutor.write(() -> createGame(name, null, null, minPlayers, maxPlayers, scoreStrategy));
    }

    @Override
    @Transactional
    public Game createGame(String name, String nameKo, String nameEn, int minPlayers, int maxPlayers, ScoreStrategy scoreStrategy) {
        if (gameRepository.existsByName(name)) {
            throw new DuplicateGameNameException(name);
        }
        Game game = Game.create(name, nameKo, nameEn, minPlayers, maxPlayers, scoreStrategy);
        return gameRepository.save(game);
    }

    @Override
    @Transactional
    public Game updateGame(Long id, String nameKo, String nameEn) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
        game.update(nameKo, nameEn);
        return gameRepository.save(game);
    }
}
