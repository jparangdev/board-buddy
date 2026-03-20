package kr.co.jparangdev.boardbuddy.application.game.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.game.usecase.GroupStatsQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.game.*;
import kr.co.jparangdev.boardbuddy.domain.game.repository.*;
import kr.co.jparangdev.boardbuddy.domain.group.exception.GroupNotFoundException;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupMemberRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.exception.UserNotGroupMemberException;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupStatsService implements GroupStatsQueryUseCase {

    private static final int TOP_LIMIT = 3;
    private static final int MIN_GAMES_FOR_WIN_RATE = 3;

    private final GameSessionRepository gameSessionRepository;
    private final GameResultRepository gameResultRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final CustomGameRepository customGameRepository;

    @Override
    public GroupStats getGroupStats(Long groupId) {
        Long currentUserId = getCurrentUserId();

        groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new UserNotGroupMemberException(groupId, currentUserId);
        }

        List<GameSession> sessions = gameSessionRepository.findAllByGroupId(groupId);

        if (sessions.isEmpty()) {
            return new GroupStats(0, 0, List.of(), List.of(), List.of(), List.of());
        }

        List<Long> sessionIds = sessions.stream().map(GameSession::getId).toList();
        List<GameResult> results = gameResultRepository.findAllBySessionIds(sessionIds);

        long totalSessions = sessions.size();
        long totalParticipations = results.size();

        Map<Long, Long> participationByUser = results.stream()
                .collect(Collectors.groupingBy(GameResult::getUserId, Collectors.counting()));

        Map<Long, Long> winsByUser = results.stream()
                .filter(GameResult::isWon)
                .collect(Collectors.groupingBy(GameResult::getUserId, Collectors.counting()));

        List<GroupStats.PlayerStat> mostActivePlayers = buildPlayerStats(participationByUser, TOP_LIMIT);
        List<GroupStats.PlayerStat> mostWins = buildPlayerStats(winsByUser, TOP_LIMIT);
        List<GroupStats.WinRateStat> winRateRanking = buildWinRateStats(participationByUser, winsByUser, MIN_GAMES_FOR_WIN_RATE, TOP_LIMIT);
        List<GroupStats.GamePlayStat> mostPlayedGames = buildGameStats(sessions, TOP_LIMIT);

        return new GroupStats(totalSessions, totalParticipations, mostActivePlayers, mostWins, winRateRanking, mostPlayedGames);
    }

    private List<GroupStats.PlayerStat> buildPlayerStats(Map<Long, Long> countByUser, int limit) {
        return countByUser.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    User user = userRepository.findById(entry.getKey()).orElse(null);
                    String nickname = user != null ? user.getNickname() : "";
                    String userTag = user != null ? user.getUserTag() : "";
                    return new GroupStats.PlayerStat(entry.getKey(), nickname, userTag, entry.getValue());
                })
                .toList();
    }

    private List<GroupStats.WinRateStat> buildWinRateStats(Map<Long, Long> participationByUser,
                                                            Map<Long, Long> winsByUser,
                                                            int minGames, int limit) {
        return participationByUser.entrySet().stream()
                .filter(entry -> entry.getValue() >= minGames)
                .map(entry -> {
                    Long userId = entry.getKey();
                    long total = entry.getValue();
                    long wins = winsByUser.getOrDefault(userId, 0L);
                    double winRate = (double) wins / total;
                    User user = userRepository.findById(userId).orElse(null);
                    String nickname = user != null ? user.getNickname() : "";
                    String userTag = user != null ? user.getUserTag() : "";
                    return new GroupStats.WinRateStat(userId, nickname, userTag, winRate, total, wins);
                })
                .sorted(Comparator.comparingDouble(GroupStats.WinRateStat::winRate).reversed())
                .limit(limit)
                .toList();
    }

    private List<GroupStats.GamePlayStat> buildGameStats(List<GameSession> sessions, int limit) {
        Map<String, Long> gamePlayCounts = sessions.stream()
                .collect(Collectors.groupingBy(this::resolveGameName, Collectors.counting()));

        return gamePlayCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> new GroupStats.GamePlayStat(entry.getKey(), entry.getValue()))
                .toList();
    }

    private String resolveGameName(GameSession session) {
        if (session.getCustomGameId() != null) {
            return customGameRepository.findById(session.getCustomGameId())
                    .map(CustomGame::getName)
                    .orElse("Unknown");
        }
        return gameRepository.findById(session.getGameId())
                .map(Game::getName)
                .orElse("Unknown");
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication is missing");
        }
        return (Long) authentication.getPrincipal();
    }
}
