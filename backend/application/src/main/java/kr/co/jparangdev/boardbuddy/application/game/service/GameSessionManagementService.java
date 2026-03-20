package kr.co.jparangdev.boardbuddy.application.game.service;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.game.usecase.GameSessionCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameSessionQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.game.*;
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
    public GameSession createSession(Long groupId, Long gameId, LocalDateTime playedAt, List<ResultInput> results) {
        Long currentUserId = getCurrentUserId();
        validateGroupMembership(groupId, currentUserId);
        validateParticipants(groupId, results);

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        GameSession session = GameSession.create(groupId, gameId, playedAt);
        GameSession savedSession = gameSessionRepository.save(session);

        List<GameResult> gameResults = calculateRanks(savedSession.getId(), results, game.getScoreStrategy());
        gameResultRepository.saveAll(gameResults);

        return savedSession;
    }

    @Override
    @Transactional
    public GameSession createSessionWithCustomGame(Long groupId, Long customGameId, LocalDateTime playedAt, List<ResultInput> results) {
        Long currentUserId = getCurrentUserId();
        validateGroupMembership(groupId, currentUserId);
        validateParticipants(groupId, results);

        CustomGame customGame = customGameRepository.findById(customGameId)
                .orElseThrow(() -> new CustomGameNotFoundException(customGameId));

        GameSession session = GameSession.createWithCustomGame(groupId, customGameId, playedAt);
        GameSession savedSession = gameSessionRepository.save(session);

        List<GameResult> gameResults = calculateRanks(savedSession.getId(), results, customGame.getScoreStrategy());
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

    private List<GameResult> calculateRanks(Long sessionId, List<ResultInput> results, ScoreStrategy strategy) {
        return switch (strategy) {
            case HIGH_WIN, LOW_WIN -> calculateScoreBasedRanks(sessionId, results, strategy);
            case RANK_ONLY -> calculateRankOnly(sessionId, results);
            case WIN_LOSE -> calculateWinLoseRanks(sessionId, results);
            case COOPERATIVE -> calculateCooperativeRanks(sessionId, results);
        };
    }

    private List<GameResult> calculateScoreBasedRanks(Long sessionId, List<ResultInput> results, ScoreStrategy strategy) {
        boolean hasScores = results.stream().anyMatch(r -> r.score() != null);

        if (!hasScores) {
            List<GameResult> gameResults = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                ResultInput input = results.get(i);
                gameResults.add(GameResult.create(sessionId, input.userId(), input.score(), Boolean.TRUE.equals(input.won()), i + 1));
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
            gameResults.add(GameResult.create(sessionId, input.userId(), input.score(), Boolean.TRUE.equals(input.won()), currentRank));
        }
        return gameResults;
    }

    private List<GameResult> calculateRankOnly(Long sessionId, List<ResultInput> results) {
        List<GameResult> gameResults = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            ResultInput input = results.get(i);
            gameResults.add(GameResult.create(sessionId, input.userId(), null, Boolean.TRUE.equals(input.won()), i + 1));
        }
        return gameResults;
    }

    private List<GameResult> calculateWinLoseRanks(Long sessionId, List<ResultInput> results) {
        List<GameResult> gameResults = new ArrayList<>();
        for (ResultInput input : results) {
            int rank = Boolean.TRUE.equals(input.won()) ? 1 : 2;
            gameResults.add(GameResult.create(sessionId, input.userId(), null, Boolean.TRUE.equals(input.won()), rank));
        }
        return gameResults;
    }

    private List<GameResult> calculateCooperativeRanks(Long sessionId, List<ResultInput> results) {
        boolean teamWon = results.stream()
                .findFirst()
                .map(r -> Boolean.TRUE.equals(r.won()))
                .orElse(false);
        int rank = teamWon ? 1 : 2;

        List<GameResult> gameResults = new ArrayList<>();
        for (ResultInput input : results) {
            gameResults.add(GameResult.create(sessionId, input.userId(), null, teamWon, rank));
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
