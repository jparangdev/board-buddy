import {useEffect, useMemo, useRef, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import type {CustomGame, Game, GroupMember} from '@/types';
import {customGameService, gameService, gameSessionService, groupService} from '@/services';
import {matchesSearch, sortAlphabetically} from '@/utils/korean';
import {getAllGameNames, getGameName} from '@/utils/game';
import styles from './CreateSessionPage.module.css';

type Step = 'game' | 'members' | 'scores' | 'confirm';

type SelectedGame = (Game | CustomGame) & { isCustom: boolean };

const STEPS: Step[] = ['game', 'members', 'scores', 'confirm'];

export function CreateSessionPage() {
  const { groupId } = useParams<{ groupId: string }>();
  const navigate = useNavigate();
  const {t, i18n} = useTranslation();
  const [step, setStep] = useState<Step>('game');

  const STEP_LABELS: Record<Step, string> = {
    game: t('session.selectGame'),
    members: t('session.selectPlayers'),
    scores: t('session.enterResults'),
    confirm: t('session.confirmSession'),
  };

  const [games, setGames] = useState<Game[]>([]);
  const [customGames, setCustomGames] = useState<CustomGame[]>([]);
  const [members, setMembers] = useState<GroupMember[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [showAddCustomGame, setShowAddCustomGame] = useState(false);
  const [customGameName, setCustomGameName] = useState('');
  const [customGameMinPlayers, setCustomGameMinPlayers] = useState(2);
  const [customGameMaxPlayers, setCustomGameMaxPlayers] = useState(4);
  const [customGameStrategy, setCustomGameStrategy] = useState('HIGH_WIN');
  const [isCreatingCustomGame, setIsCreatingCustomGame] = useState(false);
  const [showTooltip, setShowTooltip] = useState(false);

  const [selectedGame, setSelectedGame] = useState<SelectedGame | null>(null);
  const [scoreStrategy, setScoreStrategy] = useState<string>('HIGH_WIN');
  const [selectedMemberIds, setSelectedMemberIds] = useState<Set<number>>(new Set());
  const [rankOrder, setRankOrder] = useState<typeof members>([]);
  const rankDragItem = useRef<number | null>(null);
  const rankDragOverItem = useRef<number | null>(null);
  const [scores, setScores] = useState<Map<number, string>>(new Map());
  const [wonStatus, setWonStatus] = useState<Map<number, boolean>>(new Map());
  const [teamWon, setTeamWon] = useState(true);
  const [playedAt, setPlayedAt] = useState(new Date().toISOString().slice(0, 16));
  const [winnerCount, setWinnerCount] = useState(1);
  const [winPoints, setWinPoints] = useState(3);
  const [losePoints, setLosePoints] = useState(0);

  const strategy = scoreStrategy;

  useEffect(() => {
    const fetchData = async () => {
      if (!groupId) return;
      try {
        const [gamesData, customGamesData, membersData] = await Promise.all([
          gameService.getGames(),
          customGameService.getCustomGames(Number(groupId)),
          groupService.getMembers(Number(groupId)),
        ]);
        setGames(gamesData);
        setCustomGames(customGamesData);
        setMembers(membersData);
        setSelectedMemberIds(new Set(membersData.filter((m) => m.status !== 'PENDING').map((m) => m.id)));
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [groupId]);

  const filteredAndSortedGames = useMemo(() => {
    let result = games;

    if (searchQuery.trim()) {
      result = result.filter((game) =>
        getAllGameNames(game).some(name => matchesSearch(name, searchQuery))
      );
    }

    return sortAlphabetically(result, (game) => getGameName(game, i18n.language));
  }, [games, searchQuery, i18n.language]);

  const filteredAndSortedCustomGames = useMemo(() => {
    let result = customGames;

    if (searchQuery.trim()) {
      result = result.filter((game) =>
        getAllGameNames(game).some(name => matchesSearch(name, searchQuery))
      );
    }

    return sortAlphabetically(result, (game) => getGameName(game, i18n.language));
  }, [customGames, searchQuery, i18n.language]);

  const toggleMember = (id: number) => {
    const next = new Set(selectedMemberIds);
    if (next.has(id)) {
      next.delete(id);
    } else {
      next.add(id);
    }
    setSelectedMemberIds(next);
  };

  const updateScore = (userId: number, value: string) => {
    const next = new Map(scores);
    next.set(userId, value);
    setScores(next);
  };

  const toggleWon = (userId: number) => {
    const next = new Map(wonStatus);
    next.set(userId, !next.get(userId));
    setWonStatus(next);
  };

  const handleSubmit = async () => {
    if (!groupId || !selectedGame) return;
    setIsSubmitting(true);
    try {
      const results = strategy === 'RANK_ONLY'
        ? rankOrder.map((member) => ({ userId: member.id, score: null }))
        : Array.from(selectedMemberIds).map((userId) => {
            switch (strategy) {
              case 'WIN_LOSE':
                return { userId, score: null, won: wonStatus.get(userId) ?? false };
              case 'COOPERATIVE':
                return { userId, score: null, won: teamWon };
              default:
                return {
                  userId,
                  score: scores.get(userId) ? Number(scores.get(userId)) : null,
                };
            }
          });

      const sessionConfig = { scoreStrategy, winnerCount, winPoints, losePoints };
      const request = selectedGame.isCustom
        ? { customGameId: selectedGame.id, playedAt: new Date(playedAt).toISOString(), results, ...sessionConfig }
        : { gameId: selectedGame.id, playedAt: new Date(playedAt).toISOString(), results, ...sessionConfig };

      await gameSessionService.createSession(Number(groupId), request);
      navigate(`/groups/${groupId}`);
    } catch (error) {
      console.error('Failed to create session:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const currentStepIndex = STEPS.indexOf(step);

  const canProceed = () => {
    switch (step) {
      case 'game': return selectedGame !== null;
      case 'members': return selectedMemberIds.size >= 1;
      case 'scores': return true;
      case 'confirm': return true;
    }
  };

  const nextStep = () => {
    const idx = STEPS.indexOf(step);
    if (idx < STEPS.length - 1) setStep(STEPS[idx + 1]);
  };

  const prevStep = () => {
    const idx = STEPS.indexOf(step);
    if (idx > 0) setStep(STEPS[idx - 1]);
  };

  const selectedMembers = members.filter((m) => selectedMemberIds.has(m.id));

  useEffect(() => {
    setRankOrder(members.filter((m) => selectedMemberIds.has(m.id)));
  }, [selectedMemberIds, members]);

  const handleRankDragStart = (index: number) => {
    rankDragItem.current = index;
  };

  const handleRankDragEnter = (index: number) => {
    rankDragOverItem.current = index;
  };

  const handleRankDragEnd = () => {
    if (rankDragItem.current === null || rankDragOverItem.current === null) return;
    if (rankDragItem.current !== rankDragOverItem.current) {
      const updated = [...rankOrder];
      const [removed] = updated.splice(rankDragItem.current, 1);
      updated.splice(rankDragOverItem.current, 0, removed);
      setRankOrder(updated);
    }
    rankDragItem.current = null;
    rankDragOverItem.current = null;
  };

  const selectGame = (game: Game | CustomGame, isCustom: boolean) => {
    setSelectedGame({ ...game, isCustom });
    setScoreStrategy(game.scoreStrategy ?? 'HIGH_WIN');
  };

  const handleCreateCustomGame = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!groupId) return;
    setIsCreatingCustomGame(true);
    try {
      const newGame = await customGameService.createCustomGame(Number(groupId), {
        name: customGameName,
        minPlayers: customGameMinPlayers,
        maxPlayers: customGameMaxPlayers,
        scoreStrategy: customGameStrategy,
      });
      setCustomGames([...customGames, newGame]);
      setShowAddCustomGame(false);
      setCustomGameName('');
      setCustomGameMinPlayers(2);
      setCustomGameMaxPlayers(4);
      setCustomGameStrategy('HIGH_WIN');
    } catch (error) {
      console.error('Failed to create custom game:', error);
    } finally {
      setIsCreatingCustomGame(false);
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

  const getScoreHint = () => {
    switch (strategy) {
      case 'HIGH_WIN': return t('scoreStrategy.highWinHint');
      case 'LOW_WIN': return t('scoreStrategy.lowWinHint');
      case 'RANK_ONLY': return t('scoreStrategy.rankOnlyHint');
      case 'WIN_LOSE': return t('scoreStrategy.winLoseHint');
      case 'COOPERATIVE': return t('scoreStrategy.cooperativeHint');
      default: return '';
    }
  };

  const getResultSummary = (memberId: number) => {
    switch (strategy) {
      case 'WIN_LOSE':
        return wonStatus.get(memberId) ? t('scoreStrategy.won') : t('scoreStrategy.lost');
      case 'COOPERATIVE':
        return teamWon ? t('scoreStrategy.won') : t('scoreStrategy.lost');
      case 'RANK_ONLY':
        return `#${selectedMembers.findIndex(m => m.id === memberId) + 1}`;
      default: {
        const score = scores.get(memberId);
        return score ? `(${score})` : '';
      }
    }
  };

  return (
    <div className="container">
      <Link to={`/groups/${groupId}`} className={styles.backLink}>
        &larr; {t('session.backToGroup')}
      </Link>

      <div className={styles.header}>
        <h1>{t('session.recordGameSession')}</h1>
      </div>

      <div className={styles.steps}>
        {STEPS.map((s, i) => (
          <div
            key={s}
            className={`${styles.step} ${i === currentStepIndex ? styles.stepActive : ''} ${i < currentStepIndex ? styles.stepDone : ''}`}
          >
            {i + 1}. {STEP_LABELS[s]}
          </div>
        ))}
      </div>

      {step === 'game' && (
        <div className={styles.section}>
          <h2>{t('session.selectGame')}</h2>

          <div className={styles.stickyHeader}>
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
                  aria-label={t('common.clear')}
                >
                  ✕
                </button>
              )}
            </div>

            <div className={styles.addCustomGameSection}>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setShowAddCustomGame(true)}
              >
                + {t('game.addCustomGame')}
              </button>
              <div
                className={styles.tooltipWrapper}
                onMouseEnter={() => setShowTooltip(true)}
                onMouseLeave={() => setShowTooltip(false)}
              >
                <span className={styles.helpIcon}>?</span>
                {showTooltip && (
                  <div className={styles.tooltip}>
                    {t('game.customGameTooltip')}
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className={styles.gameListContainer}>
            {filteredAndSortedGames.length > 0 && (
              <>
                <h3 className={styles.gameSectionTitle}>{t('game.officialGames')}</h3>
                <div className={styles.gameGrid}>
                  {filteredAndSortedGames.map((game) => (
                    <button
                      key={`game-${game.id}`}
                      className={`${styles.gameCard} ${selectedGame?.id === game.id && !selectedGame?.isCustom ? styles.gameCardSelected : ''}`}
                      onClick={() => selectGame(game, false)}
                    >
                      <h4>{getGameName(game, i18n.language)}</h4>
                      <div className={styles.gameCardMeta}>
                        {game.minPlayers}-{game.maxPlayers} {t('game.players')}
                      </div>
                    </button>
                  ))}
                </div>
              </>
            )}

            {filteredAndSortedCustomGames.length > 0 && (
              <>
                <h3 className={styles.gameSectionTitle}>{t('game.customGames')}</h3>
                <div className={styles.gameGrid}>
                  {filteredAndSortedCustomGames.map((game) => (
                    <button
                      key={`custom-${game.id}`}
                      className={`${styles.gameCard} ${selectedGame?.id === game.id && selectedGame?.isCustom ? styles.gameCardSelected : ''}`}
                      onClick={() => selectGame(game, true)}
                    >
                      <h4>{getGameName(game, i18n.language)}</h4>
                      <div className={styles.gameCardMeta}>
                        {game.minPlayers}-{game.maxPlayers} {t('game.players')}
                      </div>
                    </button>
                  ))}
                </div>
              </>
            )}

            {filteredAndSortedGames.length === 0 && filteredAndSortedCustomGames.length === 0 && !searchQuery && (
              <p className="text-muted">{t('session.noGamesRegistered')} <Link to="/games">{t('session.addGameFirst')}</Link></p>
            )}

            {filteredAndSortedGames.length === 0 && filteredAndSortedCustomGames.length === 0 && searchQuery && (
              <div className={styles.noResults}>
                <p>{t('game.noResults', { query: searchQuery })}</p>
              </div>
            )}
          </div>
        </div>
      )}

      {step === 'members' && (
        <div className={styles.section}>
          <h2>{t('session.selectPlayers')} ({t('session.selected', { count: selectedMemberIds.size })})</h2>
          <div className={styles.memberList}>
            {members.map((member) => (
              <div
                key={member.id}
                className={`${styles.memberRow} ${selectedMemberIds.has(member.id) ? styles.memberRowSelected : ''} ${member.status === 'PENDING' ? styles.memberRowDisabled : ''}`}
                onClick={() => member.status !== 'PENDING' && toggleMember(member.id)}
              >
                <input
                  type="checkbox"
                  className={styles.checkbox}
                  checked={selectedMemberIds.has(member.id)}
                  disabled={member.status === 'PENDING'}
                  onChange={(e) => {
                    e.stopPropagation();
                    if (member.status !== 'PENDING') {
                      toggleMember(member.id);
                    }
                  }}
                />
                <div className={styles.memberAvatar}>
                  {member.nickname.charAt(0).toUpperCase()}
                </div>
                <span className={styles.memberName}>{member.nickname}</span>
                {member.status === 'PENDING' && (
                  <span className="badge badge-muted">{t('group.pendingMember')}</span>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {step === 'scores' && (
        <div className={styles.section}>
          <h2>{t('session.enterResults')}</h2>
          <div className="form-group" style={{marginBottom: 'var(--spacing-md)'}}>
            <label htmlFor="scoreStrategy">{t('game.scoreStrategy')}</label>
            <select
              id="scoreStrategy"
              className="input"
              value={scoreStrategy}
              onChange={(e) => setScoreStrategy(e.target.value)}
            >
              <option value="HIGH_WIN">{t('scoreStrategy.HIGH_WIN')}</option>
              <option value="LOW_WIN">{t('scoreStrategy.LOW_WIN')}</option>
              <option value="RANK_ONLY">{t('scoreStrategy.RANK_ONLY')}</option>
              <option value="WIN_LOSE">{t('scoreStrategy.WIN_LOSE')}</option>
              <option value="COOPERATIVE">{t('scoreStrategy.COOPERATIVE')}</option>
            </select>
          </div>
          <p className="text-muted" style={{marginBottom: 'var(--spacing-md)'}}>
            {getScoreHint()}
          </p>
          <div className="form-group" style={{marginBottom: 'var(--spacing-lg)'}}>
            <label htmlFor="playedAt">{t('session.playedAt')}</label>
            <input
              id="playedAt"
              className="input"
              type="datetime-local"
              value={playedAt}
              onChange={(e) => setPlayedAt(e.target.value)}
            />
          </div>

          {strategy === 'COOPERATIVE' && (
            <div className={styles.scoreInputs} style={{marginBottom: 'var(--spacing-md)'}}>
              <div className={styles.configRow}>
                <label>{t('session.winPoints')}</label>
                <input
                  className={`input ${styles.configInput}`}
                  type="number"
                  min={0}
                  value={winPoints}
                  onChange={(e) => setWinPoints(Number(e.target.value))}
                />
                <label>{t('session.losePoints')}</label>
                <input
                  className={`input ${styles.configInput}`}
                  type="number"
                  min={0}
                  value={losePoints}
                  onChange={(e) => setLosePoints(Number(e.target.value))}
                />
                <span className="text-muted" style={{fontSize: '0.85rem'}}>
                  {t('session.pointsHint')}
                </span>
              </div>
            </div>
          )}
          {strategy === 'COOPERATIVE' && (
            <div className={styles.cooperativeToggle}>
              <span className={styles.toggleLabel}>{t('scoreStrategy.teamResult')}</span>
              <button
                type="button"
                className={`${styles.toggleBtn} ${teamWon ? styles.toggleWon : ''}`}
                onClick={() => setTeamWon(true)}
              >
                {t('scoreStrategy.won')}
              </button>
              <button
                type="button"
                className={`${styles.toggleBtn} ${!teamWon ? styles.toggleLost : ''}`}
                onClick={() => setTeamWon(false)}
              >
                {t('scoreStrategy.lost')}
              </button>
            </div>
          )}

          {(strategy === 'HIGH_WIN' || strategy === 'LOW_WIN') && (
            <div className={styles.scoreInputs}>
              {selectedMembers.map((member) => (
                <div key={member.id} className={styles.scoreRow}>
                  <label>{member.nickname}</label>
                  <input
                    className={`input ${styles.scoreInput}`}
                    type="number"
                    placeholder={t('placeholder.score')}
                    value={scores.get(member.id) ?? ''}
                    onChange={(e) => updateScore(member.id, e.target.value)}
                  />
                </div>
              ))}
            </div>
          )}

          {strategy === 'RANK_ONLY' && (
            <div className={styles.scoreInputs}>
              <div className={styles.configRow}>
                <label>{t('session.winnerCount')}</label>
                <input
                  className={`input ${styles.configInput}`}
                  type="number"
                  min={1}
                  max={rankOrder.length}
                  value={winnerCount}
                  onChange={(e) => setWinnerCount(Math.max(1, Number(e.target.value)))}
                />
                <span className="text-muted" style={{fontSize: '0.85rem'}}>
                  {t('session.winnerCountHint', { count: winnerCount })}
                </span>
              </div>
              {rankOrder.map((member, index) => (
                <div
                  key={member.id}
                  className={styles.scoreRow}
                  draggable
                  onDragStart={() => handleRankDragStart(index)}
                  onDragEnter={() => handleRankDragEnter(index)}
                  onDragEnd={handleRankDragEnd}
                  onDragOver={(e) => e.preventDefault()}
                >
                  <span className={styles.rankNumber}>#{index + 1}</span>
                  <label>{member.nickname}</label>
                  <span className={styles.dragHandle}>⠿</span>
                </div>
              ))}
              <p className="text-muted" style={{marginTop: 'var(--spacing-sm)', fontSize: '0.85rem'}}>
                {t('scoreStrategy.rankedInOrder')}
              </p>
            </div>
          )}

          {strategy === 'WIN_LOSE' && (
            <div className={styles.scoreInputs}>
              <div className={styles.configRow}>
                <label>{t('session.winPoints')}</label>
                <input
                  className={`input ${styles.configInput}`}
                  type="number"
                  min={0}
                  value={winPoints}
                  onChange={(e) => setWinPoints(Number(e.target.value))}
                />
                <label>{t('session.losePoints')}</label>
                <input
                  className={`input ${styles.configInput}`}
                  type="number"
                  min={0}
                  value={losePoints}
                  onChange={(e) => setLosePoints(Number(e.target.value))}
                />
                <span className="text-muted" style={{fontSize: '0.85rem'}}>
                  {t('session.pointsHint')}
                </span>
              </div>
              {selectedMembers.map((member) => (
                <div key={member.id} className={styles.scoreRow}>
                  <label>{member.nickname}</label>
                  <button
                    type="button"
                    className={`${styles.toggleBtn} ${wonStatus.get(member.id) ? styles.toggleWon : styles.toggleLost}`}
                    onClick={() => toggleWon(member.id)}
                  >
                    {wonStatus.get(member.id) ? t('scoreStrategy.won') : t('scoreStrategy.lost')}
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {step === 'confirm' && (
        <div className={styles.section}>
          <h2>{t('session.confirmSession')}</h2>
          <table style={{width: '100%', borderCollapse: 'collapse'}}>
            <tbody>
              <tr>
                <td style={{padding: '8px', fontWeight: 500}}>{t('session.game')}</td>
                <td style={{padding: '8px'}}>
                  {selectedGame && getGameName(selectedGame, i18n.language)}
                  {selectedGame?.isCustom && <span className={styles.customBadge}>{t('game.custom')}</span>}
                </td>
              </tr>
              <tr>
                <td style={{padding: '8px', fontWeight: 500}}>{t('session.playedAt')}</td>
                <td style={{padding: '8px'}}>{new Date(playedAt).toLocaleString()}</td>
              </tr>
              <tr>
                <td style={{padding: '8px', fontWeight: 500}}>{t('session.players')}</td>
                <td style={{padding: '8px'}}>
                  {selectedMembers.map((m) => {
                    const summary = getResultSummary(m.id);
                    return `${m.nickname}${summary ? ` ${summary}` : ''}`;
                  }).join(', ')}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      )}

      <div className={styles.actions}>
        <button
          className="btn btn-secondary"
          onClick={prevStep}
          disabled={currentStepIndex === 0}
        >
          {t('common.previous')}
        </button>
        {step === 'confirm' ? (
          <button
            className="btn btn-primary"
            onClick={handleSubmit}
            disabled={isSubmitting}
          >
            {isSubmitting ? t('session.saving') : t('session.saveSession')}
          </button>
        ) : (
          <button
            className="btn btn-primary"
            onClick={nextStep}
            disabled={!canProceed()}
          >
            {t('common.next')}
          </button>
        )}
      </div>

      {showAddCustomGame && (
        <div className={styles.modal} onClick={() => setShowAddCustomGame(false)}>
          <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <h2>{t('game.addCustomGame')}</h2>
            <form onSubmit={handleCreateCustomGame}>
              <div className="form-group">
                <label htmlFor="customGameName">{t('game.gameName')}</label>
                <input
                  id="customGameName"
                  className="input"
                  type="text"
                  value={customGameName}
                  onChange={(e) => setCustomGameName(e.target.value)}
                  placeholder={t('placeholder.customGameName')}
                  required
                  maxLength={100}
                />
              </div>
              <div className="form-group">
                <label htmlFor="customMinPlayers">{t('game.minPlayers')}</label>
                <input
                  id="customMinPlayers"
                  className="input"
                  type="number"
                  value={customGameMinPlayers}
                  onChange={(e) => setCustomGameMinPlayers(Number(e.target.value))}
                  min={1}
                  max={20}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="customMaxPlayers">{t('game.maxPlayers')}</label>
                <input
                  id="customMaxPlayers"
                  className="input"
                  type="number"
                  value={customGameMaxPlayers}
                  onChange={(e) => setCustomGameMaxPlayers(Number(e.target.value))}
                  min={1}
                  max={20}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="customScoreStrategy">{t('game.scoreStrategy')}</label>
                <select
                  id="customScoreStrategy"
                  className="input"
                  value={customGameStrategy}
                  onChange={(e) => setCustomGameStrategy(e.target.value)}
                >
                  <option value="HIGH_WIN">{t('scoreStrategy.HIGH_WIN')}</option>
                  <option value="LOW_WIN">{t('scoreStrategy.LOW_WIN')}</option>
                  <option value="RANK_ONLY">{t('scoreStrategy.RANK_ONLY')}</option>
                  <option value="WIN_LOSE">{t('scoreStrategy.WIN_LOSE')}</option>
                  <option value="COOPERATIVE">{t('scoreStrategy.COOPERATIVE')}</option>
                </select>
              </div>
              <div className={styles.modalActions}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowAddCustomGame(false)}>
                  {t('common.cancel')}
                </button>
                <button type="submit" className="btn btn-primary" disabled={isCreatingCustomGame || !customGameName.trim()}>
                  {isCreatingCustomGame ? t('game.adding') : t('game.addGame')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
