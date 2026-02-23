package kr.co.jparangdev.boardbuddy.application.game.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import kr.co.jparangdev.boardbuddy.application.group.exception.GroupNotFoundException;
import kr.co.jparangdev.boardbuddy.application.user.exception.UserNotGroupMemberException;
import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.GameResult;
import kr.co.jparangdev.boardbuddy.domain.game.GameSession;
import kr.co.jparangdev.boardbuddy.domain.game.GroupStats;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import kr.co.jparangdev.boardbuddy.domain.game.repository.CustomGameRepository;
import kr.co.jparangdev.boardbuddy.domain.game.repository.GameRepository;
import kr.co.jparangdev.boardbuddy.domain.game.repository.GameResultRepository;
import kr.co.jparangdev.boardbuddy.domain.game.repository.GameSessionRepository;
import kr.co.jparangdev.boardbuddy.domain.group.Group;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupMemberRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class GroupStatsServiceTest {

    private GroupStatsService groupStatsService;

    @Mock private GameSessionRepository gameSessionRepository;
    @Mock private GameResultRepository gameResultRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private GroupMemberRepository groupMemberRepository;
    @Mock private UserRepository userRepository;
    @Mock private GameRepository gameRepository;
    @Mock private CustomGameRepository customGameRepository;

    @BeforeEach
    void setUp() {
        groupStatsService = new GroupStatsService(
            gameSessionRepository, gameResultRepository, groupRepository,
            groupMemberRepository, userRepository, gameRepository, customGameRepository);
    }

    private void mockSecurityContext(MockedStatic<SecurityContextHolder> holder, Long userId) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(userId);
        holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @Test
    @DisplayName("Get Group Stats Failed - Group Not Found")
    void getGroupStatsGroupNotFound() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> groupStatsService.getGroupStats(10L))
                .isInstanceOf(GroupNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Get Group Stats Failed - Not Member")
    void getGroupStatsNotMember() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.of(Group.builder().id(10L).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> groupStatsService.getGroupStats(10L))
                .isInstanceOf(UserNotGroupMemberException.class);
        }
    }

    @Test
    @DisplayName("Get Group Stats - No Sessions Returns Empty Stats")
    void getGroupStatsNoSessions() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.of(Group.builder().id(10L).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(true);
            given(gameSessionRepository.findAllByGroupId(10L)).willReturn(List.of());

            // when
            GroupStats stats = groupStatsService.getGroupStats(10L);

            // then
            assertThat(stats.totalSessions()).isEqualTo(0);
            assertThat(stats.totalParticipations()).isEqualTo(0);
            assertThat(stats.mostActivePlayers()).isEmpty();
            assertThat(stats.mostWins()).isEmpty();
            assertThat(stats.winRateRanking()).isEmpty();
            assertThat(stats.mostPlayedGames()).isEmpty();
        }
    }

    @Test
    @DisplayName("Get Group Stats - Counts Sessions And Participations")
    void getGroupStatsCountsSessionsAndParticipations() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.of(Group.builder().id(10L).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(true);

            GameSession session = GameSession.builder().id(100L).groupId(10L).gameId(1L).build();
            given(gameSessionRepository.findAllByGroupId(10L)).willReturn(List.of(session));

            GameResult r1 = GameResult.builder().sessionId(100L).userId(1L).won(true).rank(1).build();
            GameResult r2 = GameResult.builder().sessionId(100L).userId(2L).won(false).rank(2).build();
            given(gameResultRepository.findAllBySessionIds(List.of(100L))).willReturn(List.of(r1, r2));

            User user1 = User.builder().id(1L).nickname("Alice").discriminator("AA01").build();
            User user2 = User.builder().id(2L).nickname("Bob").discriminator("BB02").build();
            given(userRepository.findById(1L)).willReturn(Optional.of(user1));
            given(userRepository.findById(2L)).willReturn(Optional.of(user2));

            Game game = Game.builder().id(1L).name("Catan").scoreStrategy(ScoreStrategy.HIGH_WIN).build();
            given(gameRepository.findById(1L)).willReturn(Optional.of(game));

            // when
            GroupStats stats = groupStatsService.getGroupStats(10L);

            // then
            assertThat(stats.totalSessions()).isEqualTo(1);
            assertThat(stats.totalParticipations()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("Get Group Stats - Most Active Player Ranked First")
    void getGroupStatsMostActiveSorted() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.of(Group.builder().id(10L).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(true);

            GameSession s1 = GameSession.builder().id(100L).groupId(10L).gameId(1L).build();
            GameSession s2 = GameSession.builder().id(101L).groupId(10L).gameId(1L).build();
            given(gameSessionRepository.findAllByGroupId(10L)).willReturn(List.of(s1, s2));

            // user2 participated in both sessions, user1 only in one
            GameResult r1 = GameResult.builder().sessionId(100L).userId(1L).won(false).rank(1).build();
            GameResult r2 = GameResult.builder().sessionId(100L).userId(2L).won(true).rank(2).build();
            GameResult r3 = GameResult.builder().sessionId(101L).userId(2L).won(true).rank(1).build();
            given(gameResultRepository.findAllBySessionIds(List.of(100L, 101L))).willReturn(List.of(r1, r2, r3));

            User user1 = User.builder().id(1L).nickname("Alice").discriminator("AA01").build();
            User user2 = User.builder().id(2L).nickname("Bob").discriminator("BB02").build();
            given(userRepository.findById(1L)).willReturn(Optional.of(user1));
            given(userRepository.findById(2L)).willReturn(Optional.of(user2));

            Game game = Game.builder().id(1L).name("Catan").scoreStrategy(ScoreStrategy.HIGH_WIN).build();
            given(gameRepository.findById(1L)).willReturn(Optional.of(game));

            // when
            GroupStats stats = groupStatsService.getGroupStats(10L);

            // then
            assertThat(stats.mostActivePlayers()).isNotEmpty();
            assertThat(stats.mostActivePlayers().get(0).nickname()).isEqualTo("Bob"); // 2 sessions
            assertThat(stats.mostWins().get(0).nickname()).isEqualTo("Bob"); // 2 wins
        }
    }

    @Test
    @DisplayName("Get Group Stats - Win Rate Requires Minimum Games")
    void getGroupStatsWinRateMinGames() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.of(Group.builder().id(10L).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(true);

            GameSession session = GameSession.builder().id(100L).groupId(10L).gameId(1L).build();
            given(gameSessionRepository.findAllByGroupId(10L)).willReturn(List.of(session));

            // user1 has only 1 game — below the minimum of 3
            GameResult r1 = GameResult.builder().sessionId(100L).userId(1L).won(true).rank(1).build();
            given(gameResultRepository.findAllBySessionIds(List.of(100L))).willReturn(List.of(r1));

            User user1 = User.builder().id(1L).nickname("Alice").discriminator("AA01").build();
            given(userRepository.findById(1L)).willReturn(Optional.of(user1));

            Game game = Game.builder().id(1L).name("Catan").scoreStrategy(ScoreStrategy.HIGH_WIN).build();
            given(gameRepository.findById(1L)).willReturn(Optional.of(game));

            // when
            GroupStats stats = groupStatsService.getGroupStats(10L);

            // then
            assertThat(stats.winRateRanking()).isEmpty();
        }
    }

    @Test
    @DisplayName("Get Group Stats - Popular Games Ranked By Play Count")
    void getGroupStatsPopularGames() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            // given
            mockSecurityContext(holder, 1L);
            given(groupRepository.findById(10L)).willReturn(Optional.of(Group.builder().id(10L).build()));
            given(groupMemberRepository.existsByGroupIdAndUserId(10L, 1L)).willReturn(true);

            GameSession s1 = GameSession.builder().id(100L).groupId(10L).gameId(1L).build();
            GameSession s2 = GameSession.builder().id(101L).groupId(10L).gameId(1L).build();
            GameSession s3 = GameSession.builder().id(102L).groupId(10L).gameId(2L).build();
            given(gameSessionRepository.findAllByGroupId(10L)).willReturn(List.of(s1, s2, s3));

            GameResult r = GameResult.builder().sessionId(100L).userId(1L).won(true).rank(1).build();
            given(gameResultRepository.findAllBySessionIds(List.of(100L, 101L, 102L))).willReturn(List.of(r));

            User user1 = User.builder().id(1L).nickname("Alice").discriminator("AA01").build();
            given(userRepository.findById(1L)).willReturn(Optional.of(user1));

            given(gameRepository.findById(1L)).willReturn(Optional.of(
                Game.builder().id(1L).name("Catan").scoreStrategy(ScoreStrategy.HIGH_WIN).build()));
            given(gameRepository.findById(2L)).willReturn(Optional.of(
                Game.builder().id(2L).name("Splendor").scoreStrategy(ScoreStrategy.HIGH_WIN).build()));

            // when
            GroupStats stats = groupStatsService.getGroupStats(10L);

            // then
            assertThat(stats.mostPlayedGames()).isNotEmpty();
            assertThat(stats.mostPlayedGames().get(0).gameName()).isEqualTo("Catan"); // 2 plays
            assertThat(stats.mostPlayedGames().get(0).playCount()).isEqualTo(2);
        }
    }
}
