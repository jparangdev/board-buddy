import {type ReactNode, useEffect, useState} from 'react';
import {Link, useParams} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import type {Group, GroupStats, ScoreStatEntry} from '@/types';
import {gameSessionService, groupService} from '@/services';
import styles from './GroupDashboardPage.module.css';


function RankingSection({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div className={styles.section}>
      <h3 className={styles.sectionTitle}>{title}</h3>
      {children}
    </div>
  );
}

export function GroupDashboardPage() {
  const { id } = useParams<{ id: string }>();
  const { t } = useTranslation();
  const [group, setGroup] = useState<Group | null>(null);
  const [stats, setStats] = useState<GroupStats | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return;
      try {
        const [groupData, statsData] = await Promise.all([
          groupService.getById(Number(id)),
          gameSessionService.getGroupStats(Number(id)),
        ]);
        setGroup(groupData);
        setStats(statsData);
      } catch (error) {
        console.error('Failed to fetch dashboard data:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [id]);

  if (isLoading) {
    return (
      <div className={styles.loading}>
        <span className={styles.loadingIcon}>...</span>
        <p>{t('common.loading')}</p>
      </div>
    );
  }

  if (!group) {
    return (
      <div className="container">
        <div className={styles.notFound}>
          <h2>{t('group.notFound')}</h2>
          <Link to="/groups" className="btn btn-primary">{t('group.backToGroups')}</Link>
        </div>
      </div>
    );
  }

  const hasStats = stats && stats.totalSessions > 0;
  const averagePlayers = stats && stats.totalSessions > 0
    ? (stats.totalParticipations / stats.totalSessions)
    : 0;

  return (
    <div className="container">
      <Link to={`/groups/${id}`} className={styles.backLink}>
        &larr; {t('session.backToGroup')}
      </Link>

      <div className={styles.pageHeader}>
        <h1>{t('stats.dashboard')}</h1>
        <p className="text-muted">{group.name}</p>
      </div>

      {!hasStats ? (
        <div className={styles.emptyState}>

          <p className={styles.emptyTitle}>{t('stats.noStatsYet')}</p>
          <p className="text-muted">{t('stats.recordFirst')}</p>
          <Link to={`/groups/${id}/sessions/new`} className="btn btn-primary">
            + {t('session.recordGame')}
          </Link>
        </div>
      ) : (
        <>
          <div className={styles.summaryRow}>
            <div className={styles.summaryCard}>
              <span className={styles.summaryNumber}>{stats.totalSessions}</span>
              <span className={styles.summaryLabel}>{t('stats.sessionsLabel')}</span>
            </div>
            <div className={styles.summaryCard}>
              <span className={styles.summaryNumber}>
                {Number.isFinite(averagePlayers) ? averagePlayers.toFixed(1) : '0.0'}
              </span>
              <span className={styles.summaryLabel}>{t('stats.avgPlayersLabel')}</span>
            </div>
          </div>

          {stats.mostActivePlayers.length > 0 && (
            <RankingSection title={t('stats.mostActive')}>
              {(() => {
                const max = Math.max(...stats.mostActivePlayers.map(p => p.sessionCount ?? 0), 1);
                return stats.mostActivePlayers.map((p, i) => (
                  <div key={p.userId} className={styles.rankRow}>
                    <span className={styles.medal}>#{i + 1}</span>
                    <span className={styles.name}>{p.nickname}</span>
                    <div className={styles.barTrack}>
                      <div
                        className={`${styles.barFill} ${styles.barFillActive}`}
                        style={{ width: `${((p.sessionCount ?? 0) / max) * 100}%` }}
                      />
                    </div>
                    <span className={styles.value}>{p.sessionCount}{t('stats.sessions')}</span>
                  </div>
                ));
              })()}
            </RankingSection>
          )}

          {stats.mostWins.length > 0 && (
            <RankingSection title={t('stats.mostWins')}>
              {(() => {
                const max = Math.max(...stats.mostWins.map(p => p.winCount ?? 0), 1);
                return stats.mostWins.map((p, i) => (
                  <div key={p.userId} className={styles.rankRow}>
                    <span className={styles.medal}>#{i + 1}</span>
                    <span className={styles.name}>{p.nickname}</span>
                    <div className={styles.barTrack}>
                      <div
                        className={`${styles.barFill} ${styles.barFillWins}`}
                        style={{ width: `${((p.winCount ?? 0) / max) * 100}%` }}
                      />
                    </div>
                    <span className={styles.value}>{p.winCount}{t('stats.wins')}</span>
                  </div>
                ));
              })()}
            </RankingSection>
          )}

          <RankingSection title={t('stats.winRate')}>
            {stats.winRateRanking.length === 0 ? (
              <p className={styles.emptyNote}>{t('stats.winRateMinGames')}</p>
            ) : (
              stats.winRateRanking.map((p, i) => (
                <div key={p.userId} className={styles.rankRow}>
                  <span className={styles.medal}>#{i + 1}</span>
                  <span className={styles.name}>{p.nickname}</span>
                  <div className={styles.barTrack}>
                    <div
                      className={`${styles.barFill} ${styles.barFillRate}`}
                      style={{ width: `${(p.winRate ?? 0) * 100}%` }}
                    />
                  </div>
                  <span className={styles.value}>{Math.round((p.winRate ?? 0) * 100)}%</span>
                </div>
              ))
            )}
          </RankingSection>

          {stats.totalScoreRanking && stats.totalScoreRanking.length > 0 && (
            <RankingSection title={t('stats.totalScore')}>
              {(() => {
                const max = Math.max(...stats.totalScoreRanking.map((p: ScoreStatEntry) => p.totalScore), 1);
                return stats.totalScoreRanking.map((p: ScoreStatEntry, i: number) => (
                  <div key={p.userId} className={styles.rankRow}>
                    <span className={styles.medal}>#{i + 1}</span>
                    <span className={styles.name}>{p.nickname}</span>
                    <div className={styles.barTrack}>
                      <div
                        className={`${styles.barFill} ${styles.barFillWins}`}
                        style={{ width: `${(p.totalScore / max) * 100}%` }}
                      />
                    </div>
                    <span className={styles.value}>{t('stats.totalScoreValue', { score: p.totalScore })}</span>
                  </div>
                ));
              })()}
            </RankingSection>
          )}

          {stats.mostPlayedGames.length > 0 && (
            <RankingSection title={t('stats.popularGames')}>
              {(() => {
                const max = Math.max(...stats.mostPlayedGames.map(g => g.playCount), 1);
                return stats.mostPlayedGames.map((g, i) => (
                  <div key={g.gameName} className={styles.rankRow}>
                    <span className={styles.gameRank}>{i + 1}</span>
                    <span className={styles.name}>{g.gameName}</span>
                    <div className={styles.barTrack}>
                      <div
                        className={`${styles.barFill} ${styles.barFillGame}`}
                        style={{ width: `${(g.playCount / max) * 100}%` }}
                      />
                    </div>
                    <span className={styles.value}>{t('stats.playCount', { count: g.playCount })}</span>
                  </div>
                ));
              })()}
            </RankingSection>
          )}
        </>
      )}
    </div>
  );
}
