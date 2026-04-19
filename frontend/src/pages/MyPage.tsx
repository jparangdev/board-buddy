import {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import {useAuth} from '@/hooks/useAuth';
import {userService, authService} from '@/services';
import type {SocialAccountResponse} from '@/types';
import {startOAuthLink} from './OAuthCallbackPage';
import styles from './MyPage.module.css';

const OAUTH_REDIRECT_URI = window.location.origin + '/oauth/callback';

const PROVIDER_LABELS: Record<string, string> = {
  LOCAL: '이메일/비밀번호',
  KAKAO: '카카오',
  NAVER: '네이버',
  GOOGLE: '구글',
};

export function MyPage() {
  const {user, refreshUser, clearSession} = useAuth();
  const navigate = useNavigate();
  const {t} = useTranslation();

  const [nickname, setNickname] = useState(user?.nickname ?? '');
  const [isSaving, setIsSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);
  const [saveSuccess, setSaveSuccess] = useState(false);

  const [isDeleting, setIsDeleting] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  const [linkedAccounts, setLinkedAccounts] = useState<SocialAccountResponse[]>([]);
  const [isLinking, setIsLinking] = useState<string | null>(null);
  const [linkError, setLinkError] = useState<string | null>(null);

  useEffect(() => {
    userService.getLinkedAccounts()
      .then(setLinkedAccounts)
      .catch(() => {});
  }, []);

  if (!user) return null;

  const isLinked = (provider: string) =>
    linkedAccounts.some(a => a.provider === provider.toUpperCase());

  const handleKakaoLogin = async () => {
    setLinkError(null);
    try {
      const {authorizeUrl} = await authService.getOAuthAuthorizeUrl('kakao', OAUTH_REDIRECT_URI);
      window.location.href = authorizeUrl;
    } catch {
      setLinkError('카카오 연동 URL을 가져오지 못했습니다.');
    }
  };

  const handleLink = async (provider: string) => {
    setLinkError(null);
    setIsLinking(provider);
    try {
      startOAuthLink(provider, OAUTH_REDIRECT_URI);
      const {authorizeUrl} = await authService.getOAuthAuthorizeUrl(provider, OAUTH_REDIRECT_URI);
      window.location.href = authorizeUrl;
    } catch {
      setLinkError(`${PROVIDER_LABELS[provider.toUpperCase()] ?? provider} 연동에 실패했습니다.`);
      setIsLinking(null);
    }
  };

  const handleUnlink = async (provider: string) => {
    setLinkError(null);
    setIsLinking(provider);
    try {
      await userService.unlinkAccount(provider);
      setLinkedAccounts(prev => prev.filter(a => a.provider !== provider.toUpperCase()));
    } catch {
      setLinkError(`${PROVIDER_LABELS[provider.toUpperCase()] ?? provider} 연동 해제에 실패했습니다.`);
    } finally {
      setIsLinking(null);
    }
  };

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
      clearSession();
      navigate('/login');
    } catch {
      setIsDeleting(false);
      setShowDeleteConfirm(false);
    }
  };

  const supportedOAuthProviders = ['KAKAO'] as const;

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

          {/* Primary login method */}
          {user.provider === 'LOCAL' && (
            <div className={styles.providerItem}>
              <span className={styles.providerIcon}>🔑</span>
              <span className={styles.providerName}>{PROVIDER_LABELS.LOCAL}</span>
              <span className={styles.providerBadge}>{t('profile.connected')}</span>
            </div>
          )}

          {/* OAuth providers */}
          {supportedOAuthProviders.map(provider => {
            const linked = isLinked(provider);
            const loading = isLinking === provider;
            return (
              <div key={provider} className={styles.providerItem}>
                <span className={styles.providerIcon}>
                  {provider === 'KAKAO' ? '💬' : '🔗'}
                </span>
                <span className={styles.providerName}>{PROVIDER_LABELS[provider]}</span>
                {linked ? (
                  <>
                    <span className={styles.providerBadge}>{t('profile.connected')}</span>
                    <button
                      className="btn btn-secondary"
                      style={{marginLeft: 'auto', fontSize: '0.8rem', padding: '0.25rem 0.75rem'}}
                      onClick={() => handleUnlink(provider)}
                      disabled={loading}
                    >
                      {loading ? '처리 중...' : '연동 해제'}
                    </button>
                  </>
                ) : (
                  <button
                    className="btn btn-secondary"
                    style={{marginLeft: 'auto', fontSize: '0.8rem', padding: '0.25rem 0.75rem'}}
                    onClick={() => handleLink(provider)}
                    disabled={loading}
                  >
                    {loading ? '처리 중...' : `${PROVIDER_LABELS[provider]} 연동`}
                  </button>
                )}
              </div>
            );
          })}

          {linkError && <p className={styles.errorText}>{linkError}</p>}
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
