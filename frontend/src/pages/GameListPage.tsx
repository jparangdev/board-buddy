import {useEffect, useMemo, useState} from 'react';
import {useTranslation} from 'react-i18next';
import type {Game} from '@/types';
import {gameService} from '@/services';
import {matchesSearch, sortAlphabetically} from '@/utils/korean';
import {getAllGameNames, getGameName} from '@/utils/game';
import styles from './GameListPage.module.css';

const STRATEGY_STYLES: Record<string, string> = {
  HIGH_WIN: styles.highWin,
  LOW_WIN: styles.lowWin,
  RANK_ONLY: styles.rankOnly,
  WIN_LOSE: styles.winLose,
  COOPERATIVE: styles.cooperative,
};

export function GameListPage() {
  const {t, i18n} = useTranslation();
  const [games, setGames] = useState<Game[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [formName, setFormName] = useState('');
  const [formMinPlayers, setFormMinPlayers] = useState(2);
  const [formMaxPlayers, setFormMaxPlayers] = useState(4);
  const [formScoreStrategy, setFormScoreStrategy] = useState('HIGH_WIN');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const fetchGames = async () => {
      try {
        const data = await gameService.getGames();
        setGames(data);
      } catch (error) {
        console.error('Failed to fetch games:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchGames();
  }, []);

  const filteredAndSortedGames = useMemo(() => {
    let result = games;

    // Filter by search query
    if (searchQuery.trim()) {
      result = result.filter((game) =>
        getAllGameNames(game).some(name => matchesSearch(name, searchQuery))
      );
    }

    // Sort alphabetically
    return sortAlphabetically(result, (game) => getGameName(game, i18n.language));
  }, [games, searchQuery, i18n.language]);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const newGame = await gameService.createGame({
        name: formName,
        minPlayers: formMinPlayers,
        maxPlayers: formMaxPlayers,
        scoreStrategy: formScoreStrategy,
      });
      setGames([...games, newGame]);
      setShowCreateModal(false);
      setFormName('');
      setFormMinPlayers(2);
      setFormMaxPlayers(4);
      setFormScoreStrategy('HIGH_WIN');
    } catch (error) {
      console.error('Failed to create game:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <div className={styles.loading}>
        <span className={styles.loadingIcon}>&#x1F3B2;</span>
        <p>Loading games...</p>
      </div>
    );
  }

  return (
    <div className="container">
      <div className={styles.header}>
        <div>
          <h1>Board Games</h1>
          <p className="text-muted">Available game types for your sessions</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
          + Add Game
        </button>
      </div>

      {games.length === 0 ? (
        <div className={styles.emptyState}>
          <span className={styles.emptyIcon}>&#x1F3B2;</span>
          <h2>No games registered</h2>
          <p>Add your first board game to start recording sessions!</p>
          <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
            Add First Game
          </button>
        </div>
      ) : (
        <>
          <div className={styles.searchBox}>
            <input
              type="text"
              className="input"
              placeholder="Search games... (supports Korean chosung)"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
            {searchQuery && (
              <button
                className={styles.clearButton}
                onClick={() => setSearchQuery('')}
                aria-label="Clear search"
              >
                ✕
              </button>
            )}
          </div>

          {filteredAndSortedGames.length === 0 ? (
            <div className={styles.noResults}>
              <p>No games found matching "{searchQuery}"</p>
            </div>
          ) : (
            <div className={styles.grid}>
              {filteredAndSortedGames.map((game) => (
                <div key={game.id} className={styles.card}>
                  <h3>{getGameName(game, i18n.language)}</h3>
                  <div className={styles.cardMeta}>
                    <span>{game.minPlayers}-{game.maxPlayers} {t('game.players')}</span>
                    <span className={`${styles.strategyBadge} ${STRATEGY_STYLES[game.scoreStrategy] ?? styles.highWin}`}>
                      {t(`scoreStrategy.${game.scoreStrategy}`)}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}

      {showCreateModal && (
        <div className={styles.modal} onClick={() => setShowCreateModal(false)}>
          <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <h2>Add New Game</h2>
            <form onSubmit={handleCreate}>
              <div className="form-group">
                <label htmlFor="gameName">Game Name</label>
                <input
                  id="gameName"
                  className="input"
                  type="text"
                  value={formName}
                  onChange={(e) => setFormName(e.target.value)}
                  placeholder="e.g. Splendor, Catan"
                  required
                  maxLength={100}
                />
              </div>
              <div className="form-group">
                <label htmlFor="minPlayers">Min Players</label>
                <input
                  id="minPlayers"
                  className="input"
                  type="number"
                  value={formMinPlayers}
                  onChange={(e) => setFormMinPlayers(Number(e.target.value))}
                  min={1}
                  max={20}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="maxPlayers">Max Players</label>
                <input
                  id="maxPlayers"
                  className="input"
                  type="number"
                  value={formMaxPlayers}
                  onChange={(e) => setFormMaxPlayers(Number(e.target.value))}
                  min={1}
                  max={20}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="scoreStrategy">Score Strategy</label>
                <select
                  id="scoreStrategy"
                  className="input"
                  value={formScoreStrategy}
                  onChange={(e) => setFormScoreStrategy(e.target.value)}
                >
                  <option value="HIGH_WIN">High Score Wins</option>
                  <option value="LOW_WIN">Low Score Wins</option>
                  <option value="RANK_ONLY">Rank Only</option>
                  <option value="WIN_LOSE">Win / Lose</option>
                  <option value="COOPERATIVE">Cooperative</option>
                </select>
              </div>
              <div className={styles.modalActions}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowCreateModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary" disabled={isSubmitting || !formName.trim()}>
                  {isSubmitting ? 'Adding...' : 'Add Game'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
