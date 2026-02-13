package kr.co.jparangdev.boardbuddy.application.game.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.game.exception.GameNotFoundException;
import kr.co.jparangdev.boardbuddy.application.game.exception.GameSessionNotFoundException;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameSessionCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameSessionQueryUseCase;
import kr.co.jparangdev.boardbuddy.application.group.exception.GroupNotFoundException;
import kr.co.jparangdev.boardbuddy.application.user.exception.UserNotGroupMemberException;
import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.GameResult;
import kr.co.jparangdev.boardbuddy.domain.game.GameSession;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import kr.co.jparangdev.boardbuddy.domain.game.repository.GameRepository;
import kr.co.jparangdev.boardbuddy.domain.game.repository.GameResultRepository;
import kr.co.jparangdev.boardbuddy.domain.game.repository.GameSessionRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupMemberRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameSessionManagementService implements GameSessionQueryUseCase, GameSessionCommandUseCase {

    private final GameSessionRepository gameSessionRepository;
    private final GameResultRepository gameResultRepository;
    private final GameRepository gameRepository;
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

        // Validate group exists
        groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        // Validate current user is a group member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new UserNotGroupMemberException(groupId, currentUserId);
        }

        // Validate game exists and get score strategy
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        // Validate all participants are group members
        for (ResultInput result : results) {
            if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, result.userId())) {
                throw new UserNotGroupMemberException(groupId, result.userId());
            }
        }

        // Save session
        GameSession session = GameSession.create(groupId, gameId, playedAt);
        GameSession savedSession = gameSessionRepository.save(session);

        // Calculate ranks and save results
        List<GameResult> gameResults = calculateRanks(savedSession.getId(), results, game.getScoreStrategy());
        gameResultRepository.saveAll(gameResults);

        return savedSession;
    }

    private List<GameResult> calculateRanks(Long sessionId, List<ResultInput> results, ScoreStrategy strategy) {
        // If no scores provided, use input order as rank
        boolean hasScores = results.stream().anyMatch(r -> r.score() != null);

        if (!hasScores) {
            List<GameResult> gameResults = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                ResultInput input = results.get(i);
                gameResults.add(GameResult.create(sessionId, input.userId(), input.score(), i + 1));
            }
            return gameResults;
        }

        // Sort by score based on strategy
        List<ResultInput> sorted = new ArrayList<>(results);
        Comparator<ResultInput> comparator = Comparator.comparingInt(r -> r.score() != null ? r.score() : 0);
        if (strategy == ScoreStrategy.HIGH_WIN) {
            comparator = comparator.reversed();
        }
        sorted.sort(comparator);

        // Assign ranks with ties (same score = same rank, next rank skips)
        List<GameResult> gameResults = new ArrayList<>();
        int currentRank = 1;
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0 && !sameScore(sorted.get(i), sorted.get(i - 1))) {
                currentRank = i + 1;
            }
            ResultInput input = sorted.get(i);
            gameResults.add(GameResult.create(sessionId, input.userId(), input.score(), currentRank));
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
