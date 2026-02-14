import {Link, Outlet, useNavigate} from 'react-router-dom';
import {useEffect, useRef, useState} from 'react';
import {useAuth} from '@/hooks/useAuth';
import {authService} from '@/services';
import styles from './Layout.module.css';

export function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [isDeleting, setIsDeleting] = useState(false);
  const [showMenu, setShowMenu] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  const handleLogout = async () => {
    setShowMenu(false);
    await logout();
    navigate('/login');
  };

  const handleDeleteAccount = async () => {
    if (!globalThis.confirm('Are you sure you want to delete your account? This cannot be undone.')) return;

    setIsDeleting(true);
    try {
      await authService.deleteAccount();
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Failed to delete account:', error);
      alert('Failed to delete account. Please try again.');
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
                My Groups
              </Link>
              <Link to="/games" className={styles.navLink}>
                Games
              </Link>
              <div className={styles.userSection} ref={menuRef}>
                <button
                  className={styles.userButton}
                  onClick={() => setShowMenu(!showMenu)}
                >
                  <span className={styles.userAvatar}>
                    {user.nickname.charAt(0).toUpperCase()}
                  </span>
                  <span className={styles.userTag}>{user.userTag}</span>
                </button>
                {showMenu && (
                  <div className={styles.dropdown}>
                    <div className={styles.dropdownHeader}>
                      <span className={styles.dropdownNickname}>{user.nickname}</span>
                      <span className={styles.dropdownTag}>{user.userTag}</span>
                    </div>
                    <div className={styles.dropdownDivider} />
                    <button className={styles.dropdownItem} onClick={handleLogout}>
                      Logout
                    </button>
                    <button
                      className={`${styles.dropdownItem} ${styles.dropdownDanger}`}
                      onClick={handleDeleteAccount}
                      disabled={isDeleting}
                    >
                      {isDeleting ? 'Deleting...' : 'Delete Account'}
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
