package kr.co.jparangdev.boardbuddy.domain.game;

/**
 * Per-session rule configuration.
 *
 * <ul>
 *   <li>scoreStrategy – the scoring strategy used for this session</li>
 *   <li>winnerCount   – RANK_ONLY: top N ranks count as a win (default 1)</li>
 *   <li>winPoints     – WIN_LOSE / COOPERATIVE: points awarded to winners (default 3)</li>
 *   <li>losePoints    – WIN_LOSE / COOPERATIVE: points awarded to losers  (default 0)</li>
 * </ul>
 */
public record SessionConfig(
        ScoreStrategy scoreStrategy,
        int winnerCount,
        int winPoints,
        int losePoints
) {
    public static SessionConfig defaults() {
        return new SessionConfig(ScoreStrategy.HIGH_WIN, 1, 3, 0);
    }
}
