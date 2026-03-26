import {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import type {Invitation} from '@/types';
import {invitationService} from '@/services';
import styles from './InvitationsPage.module.css';

export function InvitationsPage() {
  const {t} = useTranslation();
  const [invitations, setInvitations] = useState<Invitation[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [processingIds, setProcessingIds] = useState<Set<number>>(new Set());

  const fetchInvitations = async () => {
    try {
      const data = await invitationService.getPendingInvitations();
      setInvitations(data);
    } catch (error) {
      console.error('Failed to fetch invitations:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchInvitations();
  }, []);

  const handleRespond = async (id: number, accept: boolean) => {
    setProcessingIds((prev) => new Set(prev).add(id));
    try {
      if (accept) {
        await invitationService.accept(id);
      } else {
        await invitationService.reject(id);
      }
      setInvitations((prev) => prev.filter((inv) => inv.id !== id));
    } catch (error) {
      console.error('Failed to respond to invitation:', error);
    } finally {
      setProcessingIds((prev) => {
        const next = new Set(prev);
        next.delete(id);
        return next;
      });
    }
  };

  if (isLoading) {
    return (
      <div className={styles.loading}>
        <span className={styles.loadingIcon}>&#x1F4EC;</span>
        <p>{t('common.loading')}</p>
      </div>
    );
  }

  return (
    <div className="container">
      <div className={styles.header}>
        <h1>{t('invitation.pendingInvitations')}</h1>
      </div>

      {invitations.length === 0 ? (
        <div className={styles.empty}>
          <span className={styles.emptyIcon}>&#x1F4EC;</span>
          <p>{t('invitation.noInvitations')}</p>
          <Link to="/groups" className="btn btn-primary">
            {t('group.backToGroups')}
          </Link>
        </div>
      ) : (
        <div className={styles.list}>
          {invitations.map((inv) => (
            <div key={inv.id} className={styles.card}>
              <div className={styles.cardInfo}>
                <div className={styles.groupName}>{inv.groupName}</div>
                <div className={styles.meta}>
                  {t('invitation.invitedBy', {name: inv.inviterNickname})}
                  <span className={styles.date}>
                    {new Date(inv.createdAt).toLocaleDateString()}
                  </span>
                </div>
              </div>
              <div className={styles.actions}>
                <button
                  className="btn btn-primary"
                  disabled={processingIds.has(inv.id)}
                  onClick={() => handleRespond(inv.id, true)}
                >
                  {t('invitation.accept')}
                </button>
                <button
                  className="btn btn-secondary"
                  disabled={processingIds.has(inv.id)}
                  onClick={() => handleRespond(inv.id, false)}
                >
                  {t('invitation.reject')}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
