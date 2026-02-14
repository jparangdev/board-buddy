import {useEffect, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import type {CustomGame, GameSession, Group, GroupMember} from '@/types';
import {customGameService, gameSessionService, groupService} from '@/services';
import {useAuth} from '@/hooks/useAuth';
import styles from './GroupDetailPage.module.css';

const STRATEGY_LABELS: Record<string, string> = {
  HIGH_WIN: 'High Wins',
  LOW_WIN: 'Low Wins',
  RANK_ONLY: 'Rank Only',
  WIN_LOSE: 'Win/Lose',
  COOPERATIVE: 'Co-op',
};

export function GroupDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [group, setGroup] = useState<Group | null>(null);
  const [members, setMembers] = useState<GroupMember[]>([]);
  const [sessions, setSessions] = useState<GameSession[]>([]);
  const [customGames, setCustomGames] = useState<CustomGame[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isDeleting, setIsDeleting] = useState(false);
  const [showCustomGameModal, setShowCustomGameModal] = useState(false);
  const [formName, setFormName] = useState('');
  const [formMinPlayers, setFormMinPlayers] = useState(2);
  const [formMaxPlayers, setFormMaxPlayers] = useState(4);
  const [formScoreStrategy, setFormScoreStrategy] = useState('HIGH_WIN');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { user } = useAuth();
  const navigate = useNavigate();

  const isOwner = group?.ownerId === user?.id;

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return;
      try {
        const [groupData, membersData, sessionsData, customGamesData] = await Promise.all([
          groupService.getById(Number(id)),
          groupService.getMembers(Number(id)),
          gameSessionService.getSessionsByGroup(Number(id)),
          customGameService.getCustomGames(Number(id)),
        ]);
        setGroup(groupData);
        setMembers(membersData);
        setSessions(sessionsData);
        setCustomGames(customGamesData);
      } catch (error) {
        console.error('Failed to fetch group:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [id]);

  const handleDeleteGroup = async () => {
    if (!group) return;
    if (!globalThis.confirm(`Are you sure you want to delete "${group.name}"? This cannot be undone.`)) return;
    setIsDeleting(true);
    try {
      await groupService.delete(group.id);
      navigate('/groups');
    } catch (error) {
      console.error('Failed to delete group:', error);
      setIsDeleting(false);
    }
  };

  const handleCreateCustomGame = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id) return;
    setIsSubmitting(true);
    try {
      const newGame = await customGameService.createCustomGame(Number(id), {
        name: formName,
        minPlayers: formMinPlayers,
        maxPlayers: formMaxPlayers,
        scoreStrategy: formScoreStrategy,
      });
      setCustomGames([...customGames, newGame]);
      setShowCustomGameModal(false);
      setFormName('');
      setFormMinPlayers(2);
      setFormMaxPlayers(4);
      setFormScoreStrategy('HIGH_WIN');
    } catch (error) {
      console.error('Failed to create custom game:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <div className={styles.loading}>
        <span className={styles.loadingIcon}>&#x1F3B2;</span>
        <p>Loading group...</p>
      </div>
    );
  }

  if (!group) {
    return (
      <div className="container">
        <div className={styles.notFound}>
          <h2>Group not found</h2>
          <Link to="/groups" className="btn btn-primary">
            Back to Groups
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <Link to="/groups" className={styles.backLink}>
        &larr; Back to Groups
      </Link>

      <div className={styles.header}>
        <div className={styles.headerInfo}>
          <div className={styles.titleRow}>
            <span className={styles.icon}>&#x265F;</span>
            <h1>{group.name}</h1>
            {isOwner && <span className="badge badge-gold">Owner</span>}
          </div>
          <p className="text-muted">
            Created on {new Date(group.createdAt).toLocaleDateString()}
          </p>
        </div>
        {isOwner && (
          <button
            className="btn btn-danger"
            onClick={handleDeleteGroup}
            disabled={isDeleting}
          >
            {isDeleting ? 'Deleting...' : 'Delete Group'}
          </button>
        )}
      </div>

      <div className={styles.section}>
        <h2>Members ({members.length})</h2>
        <div className={styles.memberList}>
          {members.map((member) => (
            <div key={member.id} className={styles.memberCard}>
              <div className={styles.avatar}>
                {member.nickname.charAt(0).toUpperCase()}
              </div>
              <div className={styles.memberInfo}>
                <span className={styles.memberName}>{member.nickname}</span>
                <span className={styles.memberTag}>{member.userTag}</span>
              </div>
              {member.id === group.ownerId && (
                <span className="badge badge-gold">Owner</span>
              )}
            </div>
          ))}
        </div>
      </div>

      <div className={styles.section} style={{marginTop: 'var(--spacing-lg)'}}>
        <div className={styles.sectionHeader}>
          <h2>Custom Games ({customGames.length})</h2>
          <button className="btn btn-primary" onClick={() => setShowCustomGameModal(true)}>
            + Add Custom Game
          </button>
        </div>
        {customGames.length === 0 ? (
          <p className="text-muted">No custom games yet. Add your group's favorite games!</p>
        ) : (
          <div className={styles.customGameList}>
            {customGames.map((game) => (
              <div key={game.id} className={styles.customGameCard}>
                <span className={styles.customGameName}>{game.name}</span>
                <span className={styles.customGameMeta}>
                  {game.minPlayers}-{game.maxPlayers} players
                </span>
                <span className={styles.customGameStrategy}>
                  {STRATEGY_LABELS[game.scoreStrategy] ?? game.scoreStrategy}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className={styles.section} style={{marginTop: 'var(--spacing-lg)'}}>
        <div className={styles.sectionHeader}>
          <h2>Game Sessions ({sessions.length})</h2>
          <Link to={`/groups/${group.id}/sessions/new`} className="btn btn-primary">
            + Record Game
          </Link>
        </div>
        {sessions.length === 0 ? (
          <p className="text-muted">No game sessions recorded yet.</p>
        ) : (
          <div className={styles.sessionList}>
            {sessions.map((session) => (
              <Link
                key={session.id}
                to={`/groups/${group.id}/sessions/${session.id}`}
                className={styles.sessionCard}
              >
                <div className={styles.sessionInfo}>
                  <span className={styles.sessionGame}>{session.gameName}</span>
                  <span className={styles.sessionDate}>
                    {new Date(session.playedAt).toLocaleString()}
                  </span>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>

      {showCustomGameModal && (
        <div className={styles.modal} onClick={() => setShowCustomGameModal(false)}>
          <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <h2>Add Custom Game</h2>
            <form onSubmit={handleCreateCustomGame}>
              <div className="form-group">
                <label htmlFor="customGameName">Game Name</label>
                <input
                  id="customGameName"
                  className="input"
                  type="text"
                  value={formName}
                  onChange={(e) => setFormName(e.target.value)}
                  placeholder="e.g. Our House Rules Chess"
                  required
                  maxLength={100}
                />
              </div>
              <div className="form-group">
                <label htmlFor="customMinPlayers">Min Players</label>
                <input
                  id="customMinPlayers"
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
                <label htmlFor="customMaxPlayers">Max Players</label>
                <input
                  id="customMaxPlayers"
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
                <label htmlFor="customScoreStrategy">Score Strategy</label>
                <select
                  id="customScoreStrategy"
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
                <button type="button" className="btn btn-secondary" onClick={() => setShowCustomGameModal(false)}>
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
