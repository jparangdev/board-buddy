package kr.co.jparangdev.boardbuddy.api.game;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import kr.co.jparangdev.boardbuddy.api.game.dto.GameSessionDto;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameQueryUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameSessionCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameSessionQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.GameResult;
import kr.co.jparangdev.boardbuddy.domain.game.GameSession;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(GameSessionController.class)
@AutoConfigureJson
class GameSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private GameSessionQueryUseCase gameSessionQueryUseCase;

    @MockitoBean
    private GameSessionCommandUseCase gameSessionCommandUseCase;

    @MockitoBean
    private GameQueryUseCase gameQueryUseCase;

    @MockitoBean
    private GameCommandUseCase gameCommandUseCase;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private GameDtoMapper mapper;

    @Test
    @DisplayName("Create Session Success")
    @WithMockUser
    void createSessionSuccess() throws Exception {
        // given
        LocalDateTime playedAt = LocalDateTime.of(2026, 2, 8, 14, 0);

        GameSessionDto.CreateRequest request = GameSessionDto.CreateRequest.builder()
                .gameId(1L)
                .playedAt(playedAt)
                .results(List.of(
                        GameSessionDto.ResultInput.builder().userId(1L).score(15).build(),
                        GameSessionDto.ResultInput.builder().userId(2L).score(10).build()
                ))
                .build();

        GameSession session = GameSession.builder()
                .id(1L).groupId(1L).gameId(1L).playedAt(playedAt).createdAt(LocalDateTime.now())
                .build();

        Game game = Game.builder()
                .id(1L).name("Splendor").scoreStrategy(ScoreStrategy.HIGH_WIN).build();

        GameSessionDto.Response response = GameSessionDto.Response.builder()
                .id(1L).groupId(1L).gameId(1L).gameName("Splendor").playedAt(playedAt)
                .build();

        given(gameSessionCommandUseCase.createSession(anyLong(), anyLong(), any(), anyList()))
                .willReturn(session);
        given(gameQueryUseCase.getGameDetail(1L)).willReturn(game);
        given(mapper.toSessionResponse(any(GameSession.class), anyString())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/groups/1/sessions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameName").value("Splendor"));
    }

    @Test
    @DisplayName("Get Sessions By Group Success")
    @WithMockUser
    void getSessionsByGroupSuccess() throws Exception {
        // given
        GameSession session = GameSession.builder()
                .id(1L).groupId(1L).gameId(1L).playedAt(LocalDateTime.now()).createdAt(LocalDateTime.now())
                .build();

        Game game = Game.builder().id(1L).name("Splendor").scoreStrategy(ScoreStrategy.HIGH_WIN).build();

        GameSessionDto.Response sessionResponse = GameSessionDto.Response.builder()
                .id(1L).groupId(1L).gameId(1L).gameName("Splendor")
                .build();

        given(gameSessionQueryUseCase.getSessionsByGroup(1L)).willReturn(List.of(session));
        given(gameQueryUseCase.getGameDetail(1L)).willReturn(game);
        given(mapper.toSessionListResponse(anyList(), anyMap()))
                .willReturn(GameSessionDto.SessionListResponse.builder()
                        .sessions(List.of(sessionResponse)).build());

        // when & then
        mockMvc.perform(get("/api/v1/groups/1/sessions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessions[0].gameName").value("Splendor"));
    }

    @Test
    @DisplayName("Get Session Detail Success")
    @WithMockUser
    void getSessionDetailSuccess() throws Exception {
        // given
        GameSession session = GameSession.builder()
                .id(1L).groupId(1L).gameId(1L).playedAt(LocalDateTime.now()).createdAt(LocalDateTime.now())
                .build();

        Game game = Game.builder().id(1L).name("Splendor").scoreStrategy(ScoreStrategy.HIGH_WIN).build();

        GameResult result = GameResult.builder()
                .id(1L).sessionId(1L).userId(1L).score(15).rank(1).build();

        User user = User.builder().id(1L).nickname("tester").discriminator("1234").build();

        GameSessionDto.DetailResponse detailResponse = GameSessionDto.DetailResponse.builder()
                .id(1L).groupId(1L).gameId(1L).gameName("Splendor")
                .results(List.of(
                        GameSessionDto.ResultResponse.builder()
                                .userId(1L).nickname("tester").userTag("tester#1234").score(15).rank(1)
                                .build()
                ))
                .build();

        given(gameSessionQueryUseCase.getSessionDetail(1L)).willReturn(session);
        given(gameSessionQueryUseCase.getSessionResults(1L)).willReturn(List.of(result));
        given(gameQueryUseCase.getGameDetail(1L)).willReturn(game);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(mapper.toSessionDetailResponse(any(), anyString(), anyList(), anyList()))
                .willReturn(detailResponse);

        // when & then
        mockMvc.perform(get("/api/v1/groups/1/sessions/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameName").value("Splendor"))
                .andExpect(jsonPath("$.results[0].rank").value(1));
    }
}
