/**
 * Session preset model.
 *
 * A preset is a named, reusable session configuration derived from a game's
 * scoring strategy. Presets are used to auto-populate the session creation
 * form when a game is selected and to adjust defaults based on player count.
 */
export interface SessionPreset {
  scoreStrategy: string;
  winnerCount: number;
  winPoints: number;
  losePoints: number;
  /**
   * Per-rank points for RANK_SCORE strategy, ordered from 1st place to last.
   * Empty array for all other strategies.
   */
  rankPoints: number[];
}

/** Base configuration per strategy (player-count-independent parts). */
const STRATEGY_BASE: Record<string, Omit<SessionPreset, 'rankPoints' | 'winnerCount'>> = {
  RANK_ONLY:   { scoreStrategy: 'RANK_ONLY',    winPoints: 0, losePoints: 0 },
  RANK_SCORE:  { scoreStrategy: 'RANK_SCORE',   winPoints: 0, losePoints: 0 },
  WIN_LOSE:    { scoreStrategy: 'WIN_LOSE',     winPoints: 3, losePoints: 0 },
  COOPERATIVE: { scoreStrategy: 'COOPERATIVE',  winPoints: 3, losePoints: 0 },
};

/**
 * Generate default rank points for N players.
 *
 * Returns `[N, N-1, ..., 1]` so the 1st-place rank earns the most points.
 *
 * @example defaultRankPoints(3) // [3, 2, 1]
 */
export function defaultRankPoints(playerCount: number): number[] {
  return Array.from({ length: playerCount }, (_, i) => playerCount - i);
}

/**
 * Compute a sensible default winner count for the given player count.
 *
 * Rules:
 * - ≤4 players  → 1 winner
 * - 5–8 players → 2 winners
 * - 9+ players  → floor(n / 4) winners (min 1)
 */
export function defaultWinnerCount(playerCount: number): number {
  if (playerCount <= 4) return 1;
  if (playerCount <= 8) return 2;
  return Math.max(1, Math.floor(playerCount / 4));
}

/**
 * Build a complete session preset for the given strategy and player count.
 *
 * This is the main entry point for auto-populating session config in the UI
 * when a game is selected or the player count changes.
 */
export function getSessionDefaults(scoreStrategy: string, playerCount: number): SessionPreset {
  const base = STRATEGY_BASE[scoreStrategy] ?? STRATEGY_BASE['RANK_ONLY'];
  const winnerCount = defaultWinnerCount(playerCount);
  const rankPoints = scoreStrategy === 'RANK_SCORE' ? defaultRankPoints(playerCount) : [];
  return { ...base, scoreStrategy, winnerCount, rankPoints };
}
