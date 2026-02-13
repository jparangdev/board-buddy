import {useEffect, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import type {Game, GroupMember} from '@/types';
import {gameService, gameSessionService, groupService} from '@/services';
import styles from './CreateSessionPage.module.css';

type Step = 'game' | 'members' | 'scores' | 'confirm';
const STEPS: Step[] = ['game', 'members', 'scores', 'confirm'];
const STEP_LABELS: Record<Step, string> = {
  game: 'Game',
  members: 'Players',
  scores: 'Scores',
  confirm: 'Confirm',
};

export function CreateSessionPage() {
  const { groupId } = useParams<{ groupId: string }>();
  const navigate = useNavigate();
  const [step, setStep] = useState<Step>('game');

  const [games, setGames] = useState<Game[]>([]);
  const [members, setMembers] = useState<GroupMember[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [selectedGame, setSelectedGame] = useState<Game | null>(null);
  const [selectedMemberIds, setSelectedMemberIds] = useState<Set<number>>(new Set());
  const [scores, setScores] = useState<Map<number, string>>(new Map());
  const [playedAt, setPlayedAt] = useState(new Date().toISOString().slice(0, 16));

  useEffect(() => {
    const fetchData = async () => {
      if (!groupId) return;
      try {
        const [gamesData, membersData] = await Promise.all([
          gameService.getGames(),
          groupService.getMembers(Number(groupId)),
        ]);
        setGames(gamesData);
        setMembers(membersData);
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [groupId]);

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

  const handleSubmit = async () => {
    if (!groupId || !selectedGame) return;
    setIsSubmitting(true);
    try {
      const results = Array.from(selectedMemberIds).map((userId) => ({
        userId,
        score: scores.get(userId) ? Number(scores.get(userId)) : null,
      }));

      await gameSessionService.createSession(Number(groupId), {
        gameId: selectedGame.id,
        playedAt: new Date(playedAt).toISOString(),
        results,
      });

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

  if (isLoading) {
    return (
      <div className={styles.loading}>
        <span className={styles.loadingIcon}>&#x1F3B2;</span>
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div className="container">
      <Link to={`/groups/${groupId}`} className={styles.backLink}>
        &larr; Back to Group
      </Link>

      <div className={styles.header}>
        <h1>Record Game Session</h1>
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
          <h2>Select a Game</h2>
          <div className={styles.gameGrid}>
            {games.map((game) => (
              <button
                key={game.id}
                className={`${styles.gameCard} ${selectedGame?.id === game.id ? styles.gameCardSelected : ''}`}
                onClick={() => setSelectedGame(game)}
              >
                <h4>{game.name}</h4>
                <div className={styles.gameCardMeta}>
                  {game.minPlayers}-{game.maxPlayers} players
                </div>
              </button>
            ))}
          </div>
          {games.length === 0 && (
            <p className="text-muted">No games registered yet. <Link to="/games">Add a game first.</Link></p>
          )}
        </div>
      )}

      {step === 'members' && (
        <div className={styles.section}>
          <h2>Select Players ({selectedMemberIds.size} selected)</h2>
          <div className={styles.memberList}>
            {members.map((member) => (
              <div
                key={member.id}
                className={`${styles.memberRow} ${selectedMemberIds.has(member.id) ? styles.memberRowSelected : ''}`}
                onClick={() => toggleMember(member.id)}
              >
                <input
                  type="checkbox"
                  className={styles.checkbox}
                  checked={selectedMemberIds.has(member.id)}
                  onChange={(e) => {
                    e.stopPropagation();
                    toggleMember(member.id);
                  }}
                />
                <div className={styles.memberAvatar}>
                  {member.nickname.charAt(0).toUpperCase()}
                </div>
                <span className={styles.memberName}>{member.nickname}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {step === 'scores' && (
        <div className={styles.section}>
          <h2>Enter Scores</h2>
          <p className="text-muted" style={{marginBottom: 'var(--spacing-md)'}}>
            {selectedGame?.scoreStrategy === 'HIGH_WIN' ? 'Highest score wins.' : 'Lowest score wins.'}
            {' '}Leave blank for rank-only recording.
          </p>
          <div className="form-group" style={{marginBottom: 'var(--spacing-lg)'}}>
            <label htmlFor="playedAt">Played At</label>
            <input
              id="playedAt"
              className="input"
              type="datetime-local"
              value={playedAt}
              onChange={(e) => setPlayedAt(e.target.value)}
            />
          </div>
          <div className={styles.scoreInputs}>
            {selectedMembers.map((member) => (
              <div key={member.id} className={styles.scoreRow}>
                <label>{member.nickname}</label>
                <input
                  className={`input ${styles.scoreInput}`}
                  type="number"
                  placeholder="Score"
                  value={scores.get(member.id) ?? ''}
                  onChange={(e) => updateScore(member.id, e.target.value)}
                />
              </div>
            ))}
          </div>
        </div>
      )}

      {step === 'confirm' && (
        <div className={styles.section}>
          <h2>Confirm Session</h2>
          <table style={{width: '100%', borderCollapse: 'collapse'}}>
            <tbody>
              <tr>
                <td style={{padding: '8px', fontWeight: 500}}>Game</td>
                <td style={{padding: '8px'}}>{selectedGame?.name}</td>
              </tr>
              <tr>
                <td style={{padding: '8px', fontWeight: 500}}>Played At</td>
                <td style={{padding: '8px'}}>{new Date(playedAt).toLocaleString()}</td>
              </tr>
              <tr>
                <td style={{padding: '8px', fontWeight: 500}}>Players</td>
                <td style={{padding: '8px'}}>
                  {selectedMembers.map((m) => {
                    const score = scores.get(m.id);
                    return `${m.nickname}${score ? ` (${score})` : ''}`;
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
          Previous
        </button>
        {step === 'confirm' ? (
          <button
            className="btn btn-primary"
            onClick={handleSubmit}
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Saving...' : 'Save Session'}
          </button>
        ) : (
          <button
            className="btn btn-primary"
            onClick={nextStep}
            disabled={!canProceed()}
          >
            Next
          </button>
        )}
      </div>
    </div>
  );
}
