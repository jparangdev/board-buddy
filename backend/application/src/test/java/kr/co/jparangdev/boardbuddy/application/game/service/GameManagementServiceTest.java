package kr.co.jparangdev.boardbuddy.application.game.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import kr.co.jparangdev.boardbuddy.domain.game.exception.DuplicateGameNameException;
import kr.co.jparangdev.boardbuddy.domain.game.exception.GameNotFoundException;
import kr.co.jparangdev.boardbuddy.domain.game.repository.GameRepository;

@ExtendWith(MockitoExtension.class)
class GameManagementServiceTest {

    private GameManagementService gameManagementService;

    @Mock
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameManagementService = new GameManagementService(gameRepository);
    }

    @Test
    @DisplayName("Create Game Success")
    void createGameSuccess() {
        // given
        String name = "Chess";
        given(gameRepository.existsByName(name)).willReturn(false);

        Game game = Game.builder().id(1L).name(name).build();
        given(gameRepository.save(any(Game.class))).willReturn(game);

        // when
        Game result = gameManagementService.createGame(name, 2, 2, ScoreStrategy.HIGH_WIN);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Create Game Failed - Duplicate Name")
    void createGameDuplicate() {
        // given
        String name = "Chess";
        given(gameRepository.existsByName(name)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> gameManagementService.createGame(name, 2, 2, ScoreStrategy.HIGH_WIN))
            .isInstanceOf(DuplicateGameNameException.class);
    }

    @Test
    @DisplayName("Get Game List Success")
    void getGameListSuccess() {
        // given
        Game game = Game.builder().id(1L).name("Chess").build();
        given(gameRepository.findAll()).willReturn(List.of(game));

        // when
        List<Game> result = gameManagementService.getGameList();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Chess");
    }

    @Test
    @DisplayName("Get Game Detail Success")
    void getGameDetailSuccess() {
        // given
        Game game = Game.builder().id(1L).name("Chess").build();
        given(gameRepository.findById(1L)).willReturn(Optional.of(game));

        // when
        Game result = gameManagementService.getGameDetail(1L);

        // then
        assertThat(result.getName()).isEqualTo("Chess");
    }

    @Test
    @DisplayName("Get Game Detail Failed - Not Found")
    void getGameDetailNotFound() {
        // given
        given(gameRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> gameManagementService.getGameDetail(1L))
            .isInstanceOf(GameNotFoundException.class);
    }
}
