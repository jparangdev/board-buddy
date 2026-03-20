package kr.co.jparangdev.boardbuddy.application.game.service;

import java.time.LocalDateTime;
import java.util.*;

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
    public GameSession createSession(Long groupId, Long gameId, LocalDateTime playedAt, List<ResultInput> results, SessionConfig config) {
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
    public GameSession createSessionWithCustomGame(Long groupId, Long customGameId, LocalDateTime playedAt, List<ResultInput> results, SessionConfig config) {
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
        return switch (strategy) {
            case HIGH_WIN, LOW_WIN -> calculateScoreBasedRanks(sessionId, results, strategy);
            case RANK_ONLY -> calculateRankOnly(sessionId, results, config.winnerCount());
            case WIN_LOSE -> calculateWinLoseRanks(sessionId, results, config.winPoints(), config.losePoints());
            case COOPERATIVE -> calculateCooperativeRanks(sessionId, results, config.winPoints(), config.losePoints());
        };
    }

    /**
     * HIGH_WIN / LOW_WIN: sort by score, auto-derive won = (rank == 1).
     */
    private List<GameResult> calculateScoreBasedRanks(Long sessionId, List<ResultInput> results, ScoreStrategy strategy) {
        boolean hasScores = results.stream().anyMatch(r -> r.score() != null);

        if (!hasScores) {
            List<GameResult> gameResults = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                ResultInput input = results.get(i);
                gameResults.add(GameResult.create(sessionId, input.userId(), null, i == 0, i + 1));
            }
            return gameResults;
        }

        List<ResultInput> sorted = new ArrayList<>(results);
        Comparator<ResultInput> comparator = Comparator.comparingInt(r -> r.score() != null ? r.score() : 0);
        if (strategy == ScoreStrategy.HIGH_WIN) {
            comparator = comparator.reversed();
        }
        sorted.sort(comparator);

        List<GameResult> gameResults = new ArrayList<>();
        int currentRank = 1;
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0 && !sameScore(sorted.get(i), sorted.get(i - 1))) {
                currentRank = i + 1;
            }
            ResultInput input = sorted.get(i);
            gameResults.add(GameResult.create(sessionId, input.userId(), input.score(), currentRank == 1, currentRank));
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
            gameResults.add(GameResult.create(sessionId, input.userId(), score, won, rank));
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
            gameResults.add(GameResult.create(sessionId, input.userId(), score, won, rank));
        }
        return gameResults;
    }

    /**
     * COOPERATIVE: entire team shares the same outcome; assign configured points as score.
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
            gameResults.add(GameResult.create(sessionId, input.userId(), score, teamWon, rank));
        }
        return gameResults;
    }

    private boolean sameScore(ResultInput a, ResultInput b) {
        if (a.score() == null && b.score() == null) return true;
        if (a.score() == null || b.score() == null) return false;
        return a.score().equals(b.score());
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication is missing");
        }
        return (Long) authentication.getPrincipal();
    }
}
