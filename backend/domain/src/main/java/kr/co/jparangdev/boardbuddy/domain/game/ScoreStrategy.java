package kr.co.jparangdev.boardbuddy.domain.game;

public enum ScoreStrategy {
    RANK_ONLY,
    WIN_LOSE,
    COOPERATIVE,
    /**
     * Rank-based scoring with user-configured points per rank position.
     * Players are ordered by rank (input order); each rank earns the points
     * specified in {@link SessionConfig#rankPoints()}.
     */
    RANK_SCORE
}
