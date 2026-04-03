package kr.co.jparangdev.boardbuddy.application.game.service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.game.usecase.GameSessionCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameSessionQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.game.*;
import kr.co.jparangdev.boardbuddy.domain.game.SessionConfig;
import kr.co.jparangdev.boardbuddy.domain.game.exception.*;
import kr.co.jparangdev.boardbuddy.domain.game.repository.*;
import kr.co.jparangdev.boardbuddy.domain.group.exception.GroupNotFoundException;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupMemberRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupRepository;
import kr.co.jparangdev.boardbuddy.domain.user.exception.UserNotGroupMemberException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameSessionManagementService implements GameSessionQueryUseCase, GameSessionCommandUseCase {
    private final GameSessionRepository gameSessionRepository;
    private final GameResultRepository gameResultRepository;
    private final GameRepository gameRepository;
    private final CustomGameRepository customGameRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public List<GameSession> getSessionsByGroup(Long groupId) {
        Long currentUserId = getCurrentUserId();

        groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new UserNotGroupMemberException(groupId, currentUserId);
        }

        return gameSessionRepository.findAllByGroupId(groupId);
    }

    @Override
    public GameSession getSessionDetail(Long sessionId) {
        return gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new GameSessionNotFoundException(sessionId));
    }

    @Override
    public List<GameResult> getSessionResults(Long sessionId) {
        gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new GameSessionNotFoundException(sessionId));
        return gameResultRepository.findAllBySessionId(sessionId);
    }

    @Override
    @Transactional
    public GameSession createSession(Long groupId, Long gameId, Instant playedAt, List<ResultInput> results, SessionConfig config) {
        Long currentUserId = getCurrentUserId();
        validateGroupMembership(groupId, currentUserId);
        validateParticipants(groupId, results);

        gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        GameSession session = GameSession.create(groupId, gameId, playedAt, config);
        GameSession savedSession = gameSessionRepository.save(session);

        List<GameResult> gameResults = calculateRanks(savedSession.getId(), results, config.scoreStrategy(), config);
        gameResultRepository.saveAll(gameResults);

        return savedSession;
    }

    @Override
    @Transactional
    public GameSession createSessionWithCustomGame(Long groupId, Long customGameId, Instant playedAt, List<ResultInput> results, SessionConfig config) {
        Long currentUserId = getCurrentUserId();
        validateGroupMembership(groupId, currentUserId);
        validateParticipants(groupId, results);

        customGameRepository.findById(customGameId)
                .orElseThrow(() -> new CustomGameNotFoundException(customGameId));

        GameSession session = GameSession.createWithCustomGame(groupId, customGameId, playedAt, config);
        GameSession savedSession = gameSessionRepository.save(session);

        List<GameResult> gameResults = calculateRanks(savedSession.getId(), results, config.scoreStrategy(), config);
        gameResultRepository.saveAll(gameResults);

        return savedSession;
    }

    private void validateGroupMembership(Long groupId, Long userId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new UserNotGroupMemberException(groupId, userId);
        }
    }

    private void validateParticipants(Long groupId, List<ResultInput> results) {
        for (ResultInput result : results) {
            if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, result.userId())) {
                throw new UserNotGroupMemberException(groupId, result.userId());
            }
        }
    }

    private List<GameResult> calculateRanks(Long sessionId, List<ResultInput> results, ScoreStrategy strategy, SessionConfig config) {
        boolean hasTeams = results.stream().anyMatch(r -> r.teamId() != null);
        if (hasTeams) {
            return calculateTeamRanks(sessionId, results, strategy, config);
        }
        return switch (strategy) {
            case RANK_ONLY -> calculateRankOnly(sessionId, results, config.winnerCount());
            case WIN_LOSE -> calculateWinLoseRanks(sessionId, results, config.winPoints(), config.losePoints());
            case COOPERATIVE -> calculateCooperativeRanks(sessionId, results, config.winPoints(), config.losePoints());
            case RANK_SCORE -> calculateRankScore(sessionId, results, config.winnerCount(), config.rankPoints());
        };
    }

    /**
     * Team mode: players sharing the same teamId form a team.
     * Teams compete against each other; all members in a team share the same rank, won, and score.
     *
     * <ul>
     *   <li>WIN_LOSE / COOPERATIVE: team outcome determined by first member's {@code won} field.</li>
     *   <li>RANK_ONLY / RANK_SCORE: teams ranked by order of first appearance in the results list.</li>
     * </ul>
     *
     * Requires at least 3 participants total (validated on the frontend; backend applies the
     * same logic regardless to keep the domain rule in one place).
     */
    private List<GameResult> calculateTeamRanks(Long sessionId, List<ResultInput> results, ScoreStrategy strategy, SessionConfig config) {
        // Preserve team insertion order via LinkedHashMap
        Map<Integer, List<ResultInput>> byTeam = results.stream()
                .collect(Collectors.groupingBy(
                        r -> r.teamId() != null ? r.teamId() : 0,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Build per-team rank, won, score
        record TeamOutcome(int rank, boolean won, Integer score) {}
        Map<Integer, TeamOutcome> teamOutcomes = new LinkedHashMap<>();

        switch (strategy) {
            case RANK_SCORE -> {
                // Teams ranked by order of first appearance; per-rank points from config
                int numTeams = byTeam.size();
                List<Integer> pts = config.rankPoints();
                int teamRank = 1;
                for (Integer teamId : byTeam.keySet()) {
                    boolean won = teamRank <= config.winnerCount();
                    int score = (pts != null && teamRank - 1 < pts.size()) ? pts.get(teamRank - 1) : (numTeams - teamRank + 1);
                    teamOutcomes.put(teamId, new TeamOutcome(teamRank, won, score));
                    teamRank++;
                }
            }
            case WIN_LOSE, COOPERATIVE -> {
                // Each team wins or loses independently; rank 1 = won, 2 = lost
                for (var entry : byTeam.entrySet()) {
                    boolean teamWon = entry.getValue().stream()
                            .findFirst()
                            .map(r -> Boolean.TRUE.equals(r.won()))
                            .orElse(false);
                    int rank = teamWon ? 1 : 2;
                    int score = teamWon ? config.winPoints() : config.losePoints();
                    teamOutcomes.put(entry.getKey(), new TeamOutcome(rank, teamWon, score));
                }
            }
            case RANK_ONLY -> {
                // Teams ranked by order of first appearance; score = numTeams - rank + 1
                int numTeams = byTeam.size();
                int teamRank = 1;
                for (Integer teamId : byTeam.keySet()) {
                    boolean won = teamRank <= config.winnerCount();
                    int score = numTeams - teamRank + 1;
                    teamOutcomes.put(teamId, new TeamOutcome(teamRank, won, score));
                    teamRank++;
                }
            }
        }

        // Build GameResult list — all members of a team share the same outcome
        List<GameResult> gameResults = new ArrayList<>();
        for (ResultInput input : results) {
            Integer tid = input.teamId() != null ? input.teamId() : 0;
            TeamOutcome outcome = teamOutcomes.get(tid);
            Integer score = outcome != null ? outcome.score() : null;
            int rank = outcome != null ? outcome.rank() : 1;
            boolean won = outcome != null && outcome.won();
            gameResults.add(GameResult.create(sessionId, input.userId(), score, won, rank, tid));
        }
        return gameResults;
    }

    /**
     * RANK_SCORE: rank by input order; assign per-rank points from the configured list.
     * Top {@code winnerCount} ranks are marked as won.
     * Falls back to (numPlayers - rank + 1) if {@code rankPoints} is shorter than the player list.
     */
    private List<GameResult> calculateRankScore(Long sessionId, List<ResultInput> results, int winnerCount, List<Integer> rankPoints) {
        int numPlayers = results.size();
        List<GameResult> gameResults = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            int rank = i + 1;
            int score = (rankPoints != null && i < rankPoints.size())
                    ? rankPoints.get(i)
                    : (numPlayers - rank + 1);
            boolean won = rank <= winnerCount;
            gameResults.add(GameResult.create(sessionId, results.get(i).userId(), score, won, rank, null));
        }
        return gameResults;
    }

    /**
     * RANK_ONLY: rank by input order, score = numPlayers - rank + 1,
     * mark top winnerCount ranks as won.
     */
    private List<GameResult> calculateRankOnly(Long sessionId, List<ResultInput> results, int winnerCount) {
        int numPlayers = results.size();
        List<GameResult> gameResults = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            int rank = i + 1;
            int score = numPlayers - rank + 1;
            boolean won = rank <= winnerCount;
            ResultInput input = results.get(i);
            gameResults.add(GameResult.create(sessionId, input.userId(), score, won, rank, null));
        }
        return gameResults;
    }

    /**
     * WIN_LOSE: rank 1 for winners, rank 2 for losers; assign configured points as score.
     */
    private List<GameResult> calculateWinLoseRanks(Long sessionId, List<ResultInput> results, int winPoints, int losePoints) {
        List<GameResult> gameResults = new ArrayList<>();
        for (ResultInput input : results) {
            boolean won = Boolean.TRUE.equals(input.won());
            int rank = won ? 1 : 2;
            int score = won ? winPoints : losePoints;
            gameResults.add(GameResult.create(sessionId, input.userId(), score, won, rank, null));
        }
        return gameResults;
    }

    /**
     * COOPERATIVE: entire group shares the same outcome; assign configured points as score.
     */
    private List<GameResult> calculateCooperativeRanks(Long sessionId, List<ResultInput> results, int winPoints, int losePoints) {
        boolean teamWon = results.stream()
                .findFirst()
                .map(r -> Boolean.TRUE.equals(r.won()))
                .orElse(false);
        int rank = teamWon ? 1 : 2;
        int score = teamWon ? winPoints : losePoints;

        List<GameResult> gameResults = new ArrayList<>();
        for (ResultInput input : results) {
            gameResults.add(GameResult.create(sessionId, input.userId(), score, teamWon, rank, null));
        }
        return gameResults;
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication is missing");
        }
        return (Long) authentication.getPrincipal();
    }
}
