import { useMemo } from 'react';
import { getSessionDefaults, type SessionPreset } from '@/utils/presets';

interface GameLike {
  scoreStrategy?: string | null;
}

/**
 * Returns auto-computed session defaults for the given game and player count.
 *
 * Recalculates whenever the game's scoring strategy or player count changes,
 * so the caller always has up-to-date defaults without manual bookkeeping.
 *
 * Returns `null` when no game is selected yet.
 */
export function useSessionDefaults(
  game: GameLike | null,
  playerCount: number,
): SessionPreset | null {
  return useMemo(() => {
    if (!game) return null;
    return getSessionDefaults(game.scoreStrategy ?? 'RANK_ONLY', playerCount);
  }, [game?.scoreStrategy, playerCount]);
}
