import {useEffect, useMemo, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import type {GameSession, Group, GroupMember, Invitation} from '@/types';
import {customGameService, gameService, gameSessionService, groupService, invitationService} from '@/services';
import {useAuth} from '@/hooks/useAuth';
import {getGameName} from '@/utils/game';
import styles from './GroupDetailPage.module.css';

export function GroupDetailPage() {
  const { id } = useParams<{ id: string }>();
  const {t, i18n} = useTranslation();
  const [group, setGroup] = useState<Group | null>(null);
  const [members, setMembers] = useState<GroupMember[]>([]);
  const [sessions, setSessions] = useState<GameSession[]>([]);
  const [rejectedInvitations, setRejectedInvitations] = useState<Invitation[]>([]);
  const [gameNames, setGameNames] = useState<Map<number, string>>(new Map());
  const [customGameNames, setCustomGameNames] = useState<Map<number, string>>(new Map());
  const [isLoading, setIsLoading] = useState(true);
  const [isDeleting, setIsDeleting] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const { user } = useAuth();
  const navigate = useNavigate();

  const isOwner = group?.ownerId === user?.id;

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return;
      try {
        const [groupData, membersData, sessionsData, gamesData, customGamesData, sentInvitations] = await Promise.all([
          groupService.getById(Number(id)),
          groupService.getMembers(Number(id)),
          gameSessionService.getSessionsByGroup(Number(id)),
          gameService.getGames(),
          customGameService.getCustomGames(Number(id)),
          invitationService.getSentInvitations(),
        ]);
        setGroup(groupData);
        setMembers(membersData);
        setSessions(sessionsData);
        setGameNames(new Map(gamesData.map((game) => [game.id, getGameName(game, i18n.language)])));
        setCustomGameNames(new Map(customGamesData.map((game) => [game.id, getGameName(game, i18n.language)])));
        const oneWeekMs = 7 * 24 * 60 * 60 * 1000;
        const now = Date.now();
        setRejectedInvitations(
          sentInvitations.filter((inv) => {
            if (inv.groupId !== groupData.id || inv.status !== 'REJECTED') return false;
            const createdAtMs = new Date(inv.createdAt).getTime();
            if (Number.isNaN(createdAtMs)) return false;
            return now - createdAtMs <= oneWeekMs;
          })
        );
      } catch (error) {
        console.error('Failed to fetch group:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [id, i18n.language]);

  const getSessionGameName = useMemo(() => {
    return (session: GameSession) => {
      if (session.customGameId && customGameNames.has(session.customGameId)) {
        return customGameNames.get(session.customGameId) ?? session.gameName;
      }
      if (session.gameId && gameNames.has(session.gameId)) {
        return gameNames.get(session.gameId) ?? session.gameName;
      }
      return session.gameName;
    };
  }, [customGameNames, gameNames]);

  const handleDeleteGroup = async () => {
    if (!group) return;
    setIsDeleting(true);
    try {
      await groupService.delete(group.id);
      navigate('/groups');
    } catch (error) {
      console.error('Failed to delete group:', error);
      setIsDeleting(false);
      setShowDeleteModal(false);
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

  if (!group) {
    return (
      <div className="container">
        <div className={styles.notFound}>
          <h2>{t('group.notFound')}</h2>
          <Link to="/groups" className="btn btn-primary">
            {t('group.backToGroups')}
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <Link to="/groups" className={styles.backLink}>
        &larr; {t('group.backToGroups')}
      </Link>

      <div className={styles.header}>
        <div className={styles.headerInfo}>
          <div className={styles.titleRow}>
            <span className={styles.icon}>&#x265F;</span>
            <h1>{group.name}</h1>
            {isOwner && <span className="badge badge-gold">{t('group.owner')}</span>}
          </div>
          <p className="text-muted">
            {t('group.createdOn')} {new Date(group.createdAt).toLocaleDateString()}
          </p>
        </div>
        <Link to={`/groups/${group.id}/dashboard`} className="btn btn-secondary">
          {t('stats.viewDashboard')}
        </Link>
      </div>

      <div className={styles.section}>
        <h2>{t('group.members')} ({members.length})</h2>
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
              {member.status === 'PENDING' && (
                <span className="badge badge-muted">{t('group.pendingMember')}</span>
              )}
              {member.id === group.ownerId && (
                <span className="badge badge-gold">{t('group.owner')}</span>
              )}
            </div>
          ))}
        </div>
        {isOwner && rejectedInvitations.length > 0 && (
          <div className={styles.rejectedSection}>
            <h3 className={styles.rejectedTitle}>{t('group.rejectedInvites')}</h3>
            <div className={styles.rejectedList}>
              {rejectedInvitations.map((inv) => (
                <div key={inv.id} className={styles.rejectedItem}>
                  <div className={styles.rejectedInfo}>
                    <span className={styles.rejectedName}>{inv.inviteeNickname ?? inv.inviteeId}</span>
                    <span className={styles.rejectedDate}>
                      {new Date(inv.createdAt).toLocaleDateString()}
                    </span>
                  </div>
                  <span className="badge badge-red">{t('invitation.statusRejected')}</span>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      <div className={styles.section} style={{marginTop: 'var(--spacing-lg)'}}>
        <div className={styles.sectionHeader}>
          <h2>{t('session.gameSessions')} ({sessions.length})</h2>
          <Link to={`/groups/${group.id}/sessions/new`} className="btn btn-primary">
            + {t('session.recordGame')}
          </Link>
        </div>
        {sessions.length === 0 ? (
          <p className="text-muted">{t('session.noSessions')}</p>
        ) : (
          <div className={styles.sessionList}>
            {sessions.map((session) => (
              <Link
                key={session.id}
                to={`/groups/${group.id}/sessions/${session.id}`}
                className={styles.sessionCard}
              >
                <div className={styles.sessionInfo}>
                  <span className={styles.sessionGame}>{getSessionGameName(session)}</span>
                  <span className={styles.sessionDate}>
                    {new Date(session.playedAt).toLocaleString()}
                  </span>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>

      {isOwner && (
        <div className={styles.deleteSection}>
          <button
            className={styles.deleteButton}
            onClick={() => setShowDeleteModal(true)}
            title="Delete group"
          >
            🗑️
          </button>
        </div>
      )}

      {showDeleteModal && (
        <div className={styles.modal} onClick={() => setShowDeleteModal(false)}>
          <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <h2>{t('group.deleteGroup')}</h2>
            <p>{t('group.deleteConfirm', {name: group.name})}</p>
            <p className="text-muted" style={{marginTop: 'var(--spacing-sm)'}}>
              {t('group.deleteWarning')}
            </p>
            <div className={styles.modalActions}>
              <button type="button" className="btn btn-secondary" onClick={() => setShowDeleteModal(false)}>
                {t('common.cancel')}
              </button>
              <button type="button" className="btn btn-danger" onClick={handleDeleteGroup} disabled={isDeleting}>
                {isDeleting ? t('group.deleting') : t('common.delete')}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
