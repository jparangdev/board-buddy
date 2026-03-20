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
        <p>{t('common.loading')}</p>
      </div>
    );
  }

  return (
    <div className="container">
      <div className={styles.header}>
        <div>
          <h1>{t('game.boardGames')}</h1>
          <p className="text-muted">{t('game.availableGames')}</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
          + {t('game.addGame')}
        </button>
      </div>

      {games.length === 0 ? (
        <div className={styles.emptyState}>
          <span className={styles.emptyIcon}>&#x1F3B2;</span>
          <h2>{t('game.noGames')}</h2>
          <p>{t('game.addFirstGame')}</p>
          <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
            {t('game.addGame')}
          </button>
        </div>
      ) : (
        <>
          <div className={styles.searchBox}>
            <input
              type="text"
              className="input"
              placeholder={t('game.searchGames')}
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
              <p>{t('game.noResults', { query: searchQuery })}</p>
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
            <h2>{t('game.addNewGame')}</h2>
            <form onSubmit={handleCreate}>
              <div className="form-group">
                <label htmlFor="gameName">{t('game.gameName')}</label>
                <input
                  id="gameName"
                  className="input"
                  type="text"
                  value={formName}
                  onChange={(e) => setFormName(e.target.value)}
                  placeholder={t('placeholder.gameName')}
                  required
                  maxLength={100}
                />
              </div>
              <div className="form-group">
                <label htmlFor="minPlayers">{t('game.minPlayers')}</label>
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
                <label htmlFor="maxPlayers">{t('game.maxPlayers')}</label>
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
                <label htmlFor="scoreStrategy">{t('game.scoreStrategy')}</label>
                <select
                  id="scoreStrategy"
                  className="input"
                  value={formScoreStrategy}
                  onChange={(e) => setFormScoreStrategy(e.target.value)}
                >
                  <option value="HIGH_WIN">{t('scoreStrategy.HIGH_WIN')}</option>
                  <option value="LOW_WIN">{t('scoreStrategy.LOW_WIN')}</option>
                  <option value="RANK_ONLY">{t('scoreStrategy.RANK_ONLY')}</option>
                  <option value="WIN_LOSE">{t('scoreStrategy.WIN_LOSE')}</option>
                  <option value="COOPERATIVE">{t('scoreStrategy.COOPERATIVE')}</option>
                </select>
              </div>
              <div className={styles.modalActions}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowCreateModal(false)}>
                  {t('common.cancel')}
                </button>
                <button type="submit" className="btn btn-primary" disabled={isSubmitting || !formName.trim()}>
                  {isSubmitting ? t('game.adding') : t('game.addGame')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
