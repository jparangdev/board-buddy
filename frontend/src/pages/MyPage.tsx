import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import {useAuth} from '@/hooks/useAuth';
import {userService, authService} from '@/services';
import styles from './MyPage.module.css';

export function MyPage() {
  const {user, refreshUser, logout} = useAuth();
  const navigate = useNavigate();
  const {t} = useTranslation();

  const [nickname, setNickname] = useState(user?.nickname ?? '');
  const [isSaving, setIsSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);
  const [saveSuccess, setSaveSuccess] = useState(false);

  const [isDeleting, setIsDeleting] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  if (!user) return null;

  const providerLabel = user.provider === 'LOCAL'
    ? t('profile.providerLocal')
    : user.provider.charAt(0) + user.provider.slice(1).toLowerCase();

  const handleUpdateNickname = async (e: React.FormEvent) => {
    e.preventDefault();
    const trimmed = nickname.trim();
    if (!trimmed || trimmed === user.nickname) return;

    setIsSaving(true);
    setSaveError(null);
    setSaveSuccess(false);
    try {
      await userService.updateNickname(trimmed);
      await refreshUser();
      setSaveSuccess(true);
    } catch {
      setSaveError(t('profile.nicknameSaveFailed'));
    } finally {
      setIsSaving(false);
    }
  };

  const handleDeleteAccount = async () => {
    setIsDeleting(true);
    try {
      await authService.deleteAccount();
      await logout();
      navigate('/login');
    } catch {
      setIsDeleting(false);
      setShowDeleteConfirm(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <div className={styles.cardHeader}>
          <div className={styles.avatar}>{user.nickname.charAt(0).toUpperCase()}</div>
          <div>
            <h1 className={styles.title}>{t('profile.title')}</h1>
            <p className={styles.userTag}>{user.userTag}</p>
          </div>
        </div>

        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>{t('profile.accountInfo')}</h2>
          <div className={styles.field}>
            <span className={styles.fieldLabel}>{t('auth.email')}</span>
            <span className={styles.fieldValue}>{user.email}</span>
          </div>
          <div className={styles.field}>
            <span className={styles.fieldLabel}>{t('auth.userTag')}</span>
            <span className={styles.fieldValue}>{user.userTag}</span>
          </div>
        </section>

        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>{t('profile.connectedAccounts')}</h2>
          <div className={styles.providerItem}>
            <span className={styles.providerIcon}>
              {user.provider === 'LOCAL' ? '🔑' : '🔗'}
            </span>
            <span className={styles.providerName}>{providerLabel}</span>
            <span className={styles.providerBadge}>{t('profile.connected')}</span>
          </div>
        </section>

        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>{t('profile.editNickname')}</h2>
          <form onSubmit={handleUpdateNickname} className={styles.nicknameForm}>
            <div className="form-group">
              <label className={styles.fieldLabel}>{t('auth.nickname')}</label>
              <input
                className="input"
                type="text"
                value={nickname}
                onChange={(e) => {
                  setNickname(e.target.value);
                  setSaveSuccess(false);
                  setSaveError(null);
                }}
                minLength={2}
                maxLength={50}
                required
              />
            </div>
            {saveError && <p className={styles.errorText}>{saveError}</p>}
            {saveSuccess && <p className={styles.successText}>{t('profile.nicknameSaved')}</p>}
            <button
              type="submit"
              className="btn btn-primary"
              disabled={isSaving || !nickname.trim() || nickname.trim() === user.nickname}
            >
              {isSaving ? t('common.loading') : t('common.save')}
            </button>
          </form>
        </section>

        <section className={`${styles.section} ${styles.dangerSection}`}>
          <h2 className={styles.sectionTitle}>{t('profile.dangerZone')}</h2>
          <p className={styles.dangerDesc}>{t('profile.deleteAccountDesc')}</p>
          <button
            className="btn btn-danger"
            onClick={() => setShowDeleteConfirm(true)}
          >
            {t('auth.deleteAccount')}
          </button>
        </section>
      </div>

      {showDeleteConfirm && (
        <div className={styles.modal} onClick={() => !isDeleting && setShowDeleteConfirm(false)}>
          <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <h2>{t('auth.deleteAccount')}</h2>
            <p>{t('auth.deleteAccountConfirm')}</p>
            <div className={styles.modalActions}>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setShowDeleteConfirm(false)}
                disabled={isDeleting}
              >
                {t('common.cancel')}
              </button>
              <button
                type="button"
                className="btn btn-danger"
                onClick={handleDeleteAccount}
                disabled={isDeleting}
              >
                {isDeleting ? t('common.deleting') : t('auth.deleteAccount')}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
