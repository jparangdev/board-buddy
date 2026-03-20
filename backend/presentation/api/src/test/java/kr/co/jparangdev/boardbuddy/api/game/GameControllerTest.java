package kr.co.jparangdev.boardbuddy.api.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import kr.co.jparangdev.boardbuddy.api.game.dto.GameDto;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(GameController.class)
@AutoConfigureJson
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private GameQueryUseCase gameQueryUseCase;

    @MockitoBean
    private GameCommandUseCase gameCommandUseCase;

    @MockitoBean
    private GameDtoMapper mapper;

    @Test
    @DisplayName("Get Game List Success")
    @WithMockUser
    void getGameListSuccess() throws Exception {
        // given
        Game game = Game.builder()
                .id(1L).name("Splendor").minPlayers(2).maxPlayers(4)
                .scoreStrategy(ScoreStrategy.HIGH_WIN).createdAt(Instant.now())
                .build();

        GameDto.Response response = GameDto.Response.builder()
                .id(1L).name("Splendor").minPlayers(2).maxPlayers(4)
                .scoreStrategy("HIGH_WIN").createdAt(Instant.now())
                .build();

        given(gameQueryUseCase.getGameList()).willReturn(List.of(game));
        given(mapper.toGameListResponse(List.of(game)))
                .willReturn(GameDto.GameListResponse.builder().games(List.of(response)).build());

        // when & then
        mockMvc.perform(get("/api/v1/games"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.games[0].name").value("Splendor"));
    }

    @Test
    @DisplayName("Get Game Detail Success")
    @WithMockUser
    void getGameDetailSuccess() throws Exception {
        // given
        Game game = Game.builder()
                .id(1L).name("Catan").minPlayers(3).maxPlayers(4)
                .scoreStrategy(ScoreStrategy.HIGH_WIN).createdAt(Instant.now())
                .build();

        GameDto.Response response = GameDto.Response.builder()
                .id(1L).name("Catan").minPlayers(3).maxPlayers(4)
                .scoreStrategy("HIGH_WIN").createdAt(Instant.now())
                .build();

        given(gameQueryUseCase.getGameDetail(1L)).willReturn(game);
        given(mapper.toGameResponse(game)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/games/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Catan"));
    }

    @Test
    @DisplayName("Create Game Success")
    @WithMockUser
    void createGameSuccess() throws Exception {
        // given
        GameDto.CreateRequest request = GameDto.CreateRequest.builder()
                .name("Splendor").minPlayers(2).maxPlayers(4).scoreStrategy("HIGH_WIN")
                .build();

        Game game = Game.builder()
                .id(1L).name("Splendor").minPlayers(2).maxPlayers(4)
                .scoreStrategy(ScoreStrategy.HIGH_WIN).createdAt(Instant.now())
                .build();

        GameDto.Response response = GameDto.Response.builder()
                .id(1L).name("Splendor").minPlayers(2).maxPlayers(4)
                .scoreStrategy("HIGH_WIN").createdAt(Instant.now())
                .build();

        given(gameCommandUseCase.createGame(any(String.class), anyInt(), anyInt(), any(ScoreStrategy.class)))
                .willReturn(game);
        given(mapper.toGameResponse(any(Game.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/games")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Splendor"));
    }
}
