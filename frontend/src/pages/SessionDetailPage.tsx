import {useEffect, useState} from 'react';
import {Link, useParams} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import type {GameSessionDetail} from '@/types';
import {gameSessionService} from '@/services';
import styles from './SessionDetailPage.module.css';

const BINARY_STRATEGIES = ['WIN_LOSE', 'COOPERATIVE'];

export function SessionDetailPage() {
  const { groupId, sessionId } = useParams<{ groupId: string; sessionId: string }>();
  const {t, i18n} = useTranslation();
  const [session, setSession] = useState<GameSessionDetail | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      if (!groupId || !sessionId) return;
      try {
        const data = await gameSessionService.getSessionDetail(Number(groupId), Number(sessionId));
        setSession(data);
      } catch (error) {
        console.error('Failed to fetch session:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [groupId, sessionId]);

  if (isLoading) {
    return (
      <div className={styles.loading}>
        <span className={styles.loadingIcon}>&#x1F3B2;</span>
        <p>{t('common.loading')}</p>
      </div>
    );
  }

  if (!session) {
    return (
      <div className="container">
        <div className={styles.notFound}>
          <h2>{t('session.notFound')}</h2>
          <Link to={`/groups/${groupId}`} className="btn btn-primary">
            {t('session.backToGroup')}
          </Link>
        </div>
      </div>
    );
  }

  const isBinaryStrategy = BINARY_STRATEGIES.includes(session.scoreStrategy);
  const isRankOnly = session.scoreStrategy === 'RANK_ONLY';
  const sortedResults = [...session.results].sort((a, b) => a.rank - b.rank);

  const rankClass = (rank: number) => {
    if (rank === 1) return styles.rank1;
    if (rank === 2) return styles.rank2;
    if (rank === 3) return styles.rank3;
    return '';
  };

  const getResultLabel = (rank: number) => {
    if (isBinaryStrategy) {
      return rank === 1 ? t('scoreStrategy.won') : t('scoreStrategy.lost');
    }
    return null;
  };

  return (
    <div className="container">
      <Link to={`/groups/${groupId}`} className={styles.backLink}>
        &larr; {t('session.backToGroup')}
      </Link>

      <div className={styles.header}>
        <div className={styles.titleRow}>
          <span className={styles.icon}>&#x1F3AF;</span>
          <h1>{session.gameName}</h1>
        </div>
        <div className={styles.meta}>
          <span>{t('session.playedOn', { date: new Date(session.playedAt).toLocaleString(i18n.language) })}</span>
          <span>{t('session.playerCount', { count: session.results.length })}</span>
        </div>
      </div>

      <div className={styles.section}>
        <h2>{t('session.results')}</h2>
        <table className={styles.resultTable}>
          <thead>
            <tr>
              <th>{isBinaryStrategy ? t('scoreStrategy.result') : t('scoreStrategy.rank')}</th>
              <th>{t('session.player')}</th>
              {!isBinaryStrategy && !isRankOnly && <th>{t('scoreStrategy.score')}</th>}
            </tr>
          </thead>
          <tbody>
            {sortedResults.map((result) => (
              <tr key={result.userId}>
                <td className={`${styles.rankCell} ${rankClass(result.rank)}`}>
                  {isBinaryStrategy
                    ? getResultLabel(result.rank)
                    : `#${result.rank}`
                  }
                </td>
                <td>{result.nickname ?? `User #${result.userId}`}</td>
                {!isBinaryStrategy && !isRankOnly && (
                  <td>{result.score ?? '-'}</td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
