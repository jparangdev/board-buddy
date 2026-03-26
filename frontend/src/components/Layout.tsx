import {Link, Outlet, useNavigate} from 'react-router-dom';
import {useEffect, useRef, useState} from 'react';
import {useTranslation} from 'react-i18next';
import {useAuth} from '@/hooks/useAuth';
import {authService, invitationService} from '@/services';
import styles from './Layout.module.css';

export function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const {t, i18n} = useTranslation();

  const toggleLanguage = () => {
    const newLang = i18n.language === 'ko' ? 'en' : 'ko';
    i18n.changeLanguage(newLang);
    localStorage.setItem('language', newLang);
  };
  const [isDeleting, setIsDeleting] = useState(false);
  const [showMenu, setShowMenu] = useState(false);
  const [pendingCount, setPendingCount] = useState(0);
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!user) return;
    invitationService.getPendingInvitations()
      .then((invitations) => setPendingCount(invitations.length))
      .catch(() => setPendingCount(0));
  }, [user]);

  const handleLogout = async () => {
    setShowMenu(false);
    await logout();
    navigate('/login');
  };

  const handleDeleteAccount = async () => {
    if (!globalThis.confirm(t('auth.deleteAccountConfirm') || 'Are you sure you want to delete your account? This cannot be undone.')) return;

    setIsDeleting(true);
    try {
      await authService.deleteAccount();
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Failed to delete account:', error);
      alert(t('auth.deleteFailed') || 'Failed to delete account. Please try again.');
    } finally {
      setIsDeleting(false);
      setShowMenu(false);
    }
  };

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setShowMenu(false);
      }
    };
    if (showMenu) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [showMenu]);

  return (
    <div className={styles.layout}>
      <header className={styles.header}>
        <div className={styles.headerContent}>
          <Link to="/" className={styles.logo}>
            <span className={styles.logoIcon}>&#x1F3B2;</span>
            Board Buddy
          </Link>
          {user && (
            <nav className={styles.nav}>
              <Link to="/groups" className={styles.navLink}>
                {t('group.myGroups')}
              </Link>
              <Link to="/games" className={styles.navLink}>
                {t('nav.games')}
              </Link>
              <Link to="/invitations" className={styles.navLink} style={{position: 'relative'}}>
                {t('nav.invitations')}
                {pendingCount > 0 && (
                  <span className={styles.badge}>{pendingCount}</span>
                )}
              </Link>
              <div className={styles.userSection} ref={menuRef}>
                <button
                  className={styles.userButton}
                  onClick={() => setShowMenu(!showMenu)}
                >
                  <span className={styles.userAvatar}>
                    {user.nickname.charAt(0).toUpperCase()}
                  </span>
                  <span className={styles.userNickname}>{user.nickname}</span>
                </button>
                {showMenu && (
                  <div className={styles.dropdown}>
                    <div className={styles.dropdownHeader}>
                      <span className={styles.dropdownNickname}>{user.nickname}</span>
                      <span className={styles.dropdownTag}>{user.userTag}</span>
                    </div>
                    <div className={styles.dropdownDivider} />
                    <button className={styles.dropdownItem} onClick={() => { toggleLanguage(); setShowMenu(false); }}>
                      {i18n.language === 'ko' ? '🌐 한국어' : '🌐 English'}
                    </button>
                    <div className={styles.dropdownDivider} />
                    <button className={styles.dropdownItem} onClick={handleLogout}>
                      {t('nav.logout')}
                    </button>
                    <button
                      className={`${styles.dropdownItem} ${styles.dropdownDanger}`}
                      onClick={handleDeleteAccount}
                      disabled={isDeleting}
                    >
                      {isDeleting ? t('common.deleting') : t('auth.deleteAccount')}
                    </button>
                  </div>
                )}
              </div>
            </nav>
          )}
        </div>
      </header>
      <main className={styles.main}>
        <Outlet />
      </main>
      <footer className={styles.footer}>
        <p>Board Buddy - Find your game night crew</p>
      </footer>
    </div>
  );
}
