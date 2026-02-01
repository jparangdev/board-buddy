import {Link, Outlet, useNavigate} from 'react-router-dom';
import {useAuth} from '@/hooks/useAuth';
import styles from './Layout.module.css';

export function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

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
              <div className={styles.userSection}>
                <span className={styles.userTag}>{user.userTag}</span>
                <button onClick={handleLogout} className="btn btn-secondary">
                  Logout
                </button>
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
