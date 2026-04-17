import {Link, Outlet, useNavigate} from 'react-router-dom';
import {useEffect, useRef, useState} from 'react';
import {useTranslation} from 'react-i18next';
import {useAuth} from '@/hooks/useAuth';
import {invitationService} from '@/services';
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
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const [showMenu, setShowMenu] = useState(false);
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const [pendingCount, setPendingCount] = useState(0);
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!user) return;
    invitationService.getPendingInvitations()
      .then((invitations) => setPendingCount(invitations.length))
      .catch(() => setPendingCount(0));
  }, [user]);

  const handleLogout = () => {
    setShowMenu(false);
    setShowLogoutModal(true);
  };

  const handleConfirmLogout = async () => {
    setIsLoggingOut(true);
    try {
      await logout();
      navigate('/login');
    } finally {
      setIsLoggingOut(false);
      setShowLogoutModal(false);
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
                    <Link
                      to="/profile"
                      className={styles.dropdownItem}
                      onClick={() => setShowMenu(false)}
                    >
                      {t('nav.profile')}
                    </Link>
                    <button className={styles.dropdownItem} onClick={() => { toggleLanguage(); setShowMenu(false); }}>
                      {i18n.language === 'ko' ? '🌐 한국어' : '🌐 English'}
                    </button>
                    <div className={styles.dropdownDivider} />
                    <button className={styles.dropdownItem} onClick={handleLogout}>
                      {t('nav.logout')}
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

      {showLogoutModal && (
        <div className={styles.modal} onClick={() => setShowLogoutModal(false)}>
          <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <h2>{t('auth.logoutTitle')}</h2>
            <p>{t('auth.logoutConfirm')}</p>
            <div className={styles.modalActions}>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setShowLogoutModal(false)}
                disabled={isLoggingOut}
              >
                {t('common.cancel')}
              </button>
              <button
                type="button"
                className="btn btn-primary"
                onClick={handleConfirmLogout}
                disabled={isLoggingOut}
              >
                {isLoggingOut ? t('common.loading') : t('nav.logout')}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
