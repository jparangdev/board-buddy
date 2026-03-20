package kr.co.jparangdev.boardbuddy.application.game.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import kr.co.jparangdev.boardbuddy.application.game.usecase.GameSessionCommandUseCase.ResultInput;
import kr.co.jparangdev.boardbuddy.domain.game.*;
import kr.co.jparangdev.boardbuddy.domain.game.SessionConfig;
import kr.co.jparangdev.boardbuddy.domain.game.exception.GameSessionNotFoundException;
import kr.co.jparangdev.boardbuddy.domain.game.repository.*;
import kr.co.jparangdev.boardbuddy.domain.group.Group;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupMemberRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupRepository;

@ExtendWith(MockitoExtension.class)
class GameSessionManagementServiceTest {

    private GameSessionManagementService gameSessionManagementService;

    @Mock
    private GameSessionRepository gameSessionRepository;
    @Mock
    private GameResultRepository gameResultRepository;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private CustomGameRepository customGameRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GroupMemberRepository groupMemberRepository;

    @BeforeEach
    void setUp() {
        gameSessionManagementService = new GameSessionManagementService(
            gameSessionRepository, gameResultRepository, gameRepository, customGameRepository, groupRepository, groupMemberRepository
        );
    }

    @Test
    @DisplayName("Create Session - High Win Strategy")
    void createSessionHighWin() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            // given
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(1L);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Long groupId = 1L;
            Long gameId = 1L;
            given(groupRepository.findById(groupId)).willReturn(Optional.of(Group.builder().id(groupId).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 1L)).willReturn(true);

            Game game = Game.builder().id(gameId).scoreStrategy(ScoreStrategy.HIGH_WIN).build();
            given(gameRepository.findById(gameId)).willReturn(Optional.of(game));

            ResultInput user1 = new ResultInput(2L, 100, null);
            ResultInput user2 = new ResultInput(3L, 200, null);
            List<ResultInput> results = List.of(user1, user2);

            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 2L)).willReturn(true);
            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 3L)).willReturn(true);

            GameSession session = GameSession.builder().id(10L).build();
            given(gameSessionRepository.save(any(GameSession.class))).willReturn(session);

            // when
            SessionConfig config = new SessionConfig(ScoreStrategy.HIGH_WIN, 1, 3, 0);
            gameSessionManagementService.createSession(groupId, gameId, LocalDateTime.now(), results, config);

            // then
            ArgumentCaptor<List<GameResult>> captor = ArgumentCaptor.forClass(List.class);
            verify(gameResultRepository).saveAll(captor.capture());

            List<GameResult> savedResults = captor.getValue();
            assertThat(savedResults).hasSize(2);

            // user2 (200) -> rank 1, won = true
            GameResult result2 = savedResults.stream().filter(r -> r.getUserId().equals(3L)).findFirst().get();
            assertThat(result2.getRank()).isEqualTo(1);
            assertThat(result2.isWon()).isTrue();

            // user1 (100) -> rank 2, won = false
            GameResult result1 = savedResults.stream().filter(r -> r.getUserId().equals(2L)).findFirst().get();
            assertThat(result1.getRank()).isEqualTo(2);
            assertThat(result1.isWon()).isFalse();
        }
    }

    @Test
    @DisplayName("Create Session - Low Win Strategy")
    void createSessionLowWin() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            // given
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(1L);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Long groupId = 1L;
            Long gameId = 1L;
            given(groupRepository.findById(groupId)).willReturn(Optional.of(Group.builder().id(groupId).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 1L)).willReturn(true);

            Game game = Game.builder().id(gameId).scoreStrategy(ScoreStrategy.LOW_WIN).build();
            given(gameRepository.findById(gameId)).willReturn(Optional.of(game));

            ResultInput user1 = new ResultInput(2L, 10, null);
            ResultInput user2 = new ResultInput(3L, 20, null);
            List<ResultInput> results = List.of(user1, user2);

            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 2L)).willReturn(true);
            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 3L)).willReturn(true);

            GameSession session = GameSession.builder().id(10L).build();
            given(gameSessionRepository.save(any(GameSession.class))).willReturn(session);

            // when
            SessionConfig config = new SessionConfig(ScoreStrategy.LOW_WIN, 1, 3, 0);
            gameSessionManagementService.createSession(groupId, gameId, LocalDateTime.now(), results, config);

            // then
            ArgumentCaptor<List<GameResult>> captor = ArgumentCaptor.forClass(List.class);
            verify(gameResultRepository).saveAll(captor.capture());

            List<GameResult> savedResults = captor.getValue();

            // user1 (10) -> rank 1, won = true (Low Win)
            GameResult result1 = savedResults.stream().filter(r -> r.getUserId().equals(2L)).findFirst().get();
            assertThat(result1.getRank()).isEqualTo(1);
            assertThat(result1.isWon()).isTrue();
        }
    }

    @Test
    @DisplayName("Create Session - Rank Only: score by rank inversion, top winnerCount wins")
    void createSessionRankOnly() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(1L);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Long groupId = 1L;
            Long gameId = 1L;
            given(groupRepository.findById(groupId)).willReturn(Optional.of(Group.builder().id(groupId).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 1L)).willReturn(true);

            Game game = Game.builder().id(gameId).scoreStrategy(ScoreStrategy.RANK_ONLY).build();
            given(gameRepository.findById(gameId)).willReturn(Optional.of(game));

            // Input order = ranking order (1st, 2nd, 3rd)
            List<ResultInput> results = List.of(
                    new ResultInput(2L, null, null),
                    new ResultInput(3L, null, null),
                    new ResultInput(4L, null, null)
            );
            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 2L)).willReturn(true);
            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 3L)).willReturn(true);
            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 4L)).willReturn(true);

            GameSession session = GameSession.builder().id(10L).build();
            given(gameSessionRepository.save(any(GameSession.class))).willReturn(session);

            // winnerCount = 2 (top 2 win)
            SessionConfig config = new SessionConfig(ScoreStrategy.RANK_ONLY, 2, 3, 0);
            gameSessionManagementService.createSession(groupId, gameId, LocalDateTime.now(), results, config);

            ArgumentCaptor<List<GameResult>> captor = ArgumentCaptor.forClass(List.class);
            verify(gameResultRepository).saveAll(captor.capture());
            List<GameResult> savedResults = captor.getValue();

            GameResult first = savedResults.stream().filter(r -> r.getUserId().equals(2L)).findFirst().get();
            assertThat(first.getRank()).isEqualTo(1);
            assertThat(first.getScore()).isEqualTo(3); // 3 - 1 + 1
            assertThat(first.isWon()).isTrue();

            GameResult second = savedResults.stream().filter(r -> r.getUserId().equals(3L)).findFirst().get();
            assertThat(second.getRank()).isEqualTo(2);
            assertThat(second.getScore()).isEqualTo(2);
            assertThat(second.isWon()).isTrue();

            GameResult third = savedResults.stream().filter(r -> r.getUserId().equals(4L)).findFirst().get();
            assertThat(third.getRank()).isEqualTo(3);
            assertThat(third.getScore()).isEqualTo(1);
            assertThat(third.isWon()).isFalse();
        }
    }

    @Test
    @DisplayName("Create Session - Win/Lose: assigns configured points as score")
    void createSessionWinLose() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(1L);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Long groupId = 1L;
            Long gameId = 1L;
            given(groupRepository.findById(groupId)).willReturn(Optional.of(Group.builder().id(groupId).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 1L)).willReturn(true);

            Game game = Game.builder().id(gameId).scoreStrategy(ScoreStrategy.WIN_LOSE).build();
            given(gameRepository.findById(gameId)).willReturn(Optional.of(game));

            List<ResultInput> results = List.of(
                    new ResultInput(2L, null, true),
                    new ResultInput(3L, null, false)
            );
            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 2L)).willReturn(true);
            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 3L)).willReturn(true);

            GameSession session = GameSession.builder().id(10L).build();
            given(gameSessionRepository.save(any(GameSession.class))).willReturn(session);

            SessionConfig config = new SessionConfig(ScoreStrategy.WIN_LOSE, 1, 5, 1);
            gameSessionManagementService.createSession(groupId, gameId, LocalDateTime.now(), results, config);

            ArgumentCaptor<List<GameResult>> captor = ArgumentCaptor.forClass(List.class);
            verify(gameResultRepository).saveAll(captor.capture());
            List<GameResult> savedResults = captor.getValue();

            GameResult winner = savedResults.stream().filter(r -> r.getUserId().equals(2L)).findFirst().get();
            assertThat(winner.isWon()).isTrue();
            assertThat(winner.getScore()).isEqualTo(5);
            assertThat(winner.getRank()).isEqualTo(1);

            GameResult loser = savedResults.stream().filter(r -> r.getUserId().equals(3L)).findFirst().get();
            assertThat(loser.isWon()).isFalse();
            assertThat(loser.getScore()).isEqualTo(1);
            assertThat(loser.getRank()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("Get Sessions By Group Success")
    void getSessionsByGroupSuccess() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            // given
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(1L);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Long groupId = 1L;
            given(groupRepository.findById(groupId)).willReturn(Optional.of(Group.builder().id(groupId).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(groupId, 1L)).willReturn(true);

            GameSession session = GameSession.builder().id(10L).groupId(groupId).build();
            given(gameSessionRepository.findAllByGroupId(groupId)).willReturn(List.of(session));

            // when
            List<GameSession> result = gameSessionManagementService.getSessionsByGroup(groupId);

            // then
            assertThat(result).hasSize(1);
        }
    }

    @Test
    @DisplayName("Get Session Detail Success")
    void getSessionDetailSuccess() {
        Long sessionId = 10L;
        GameSession session = GameSession.builder().id(sessionId).build();
        given(gameSessionRepository.findById(sessionId)).willReturn(Optional.of(session));

        GameSession result = gameSessionManagementService.getSessionDetail(sessionId);

        assertThat(result.getId()).isEqualTo(sessionId);
    }

    @Test
    @DisplayName("Get Session Detail Failed - Not Found")
    void getSessionDetailNotFound() {
        Long sessionId = 10L;
        given(gameSessionRepository.findById(sessionId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> gameSessionManagementService.getSessionDetail(sessionId))
            .isInstanceOf(GameSessionNotFoundException.class);
    }
}
