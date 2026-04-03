package kr.co.jparangdev.boardbuddy.domain.game;

import java.util.List;

/**
 * Per-session rule configuration.
 *
 * <ul>
 *   <li>scoreStrategy – the scoring strategy used for this session</li>
 *   <li>winnerCount   – RANK_ONLY / RANK_SCORE: top N ranks count as a win (default 1)</li>
 *   <li>winPoints     – WIN_LOSE / COOPERATIVE: points awarded to winners (default 3)</li>
 *   <li>losePoints    – WIN_LOSE / COOPERATIVE: points awarded to losers  (default 0)</li>
 *   <li>rankPoints    – RANK_SCORE: points per rank position (index 0 = 1st place).
 *                       Empty list falls back to auto-calculation (numPlayers - rank + 1).</li>
 * </ul>
 */
public record SessionConfig(
        ScoreStrategy scoreStrategy,
        int winnerCount,
        int winPoints,
        int losePoints,
        List<Integer> rankPoints
) {
    public static SessionConfig defaults() {
        return new SessionConfig(ScoreStrategy.RANK_ONLY, 1, 3, 0, List.of());
    }
}
