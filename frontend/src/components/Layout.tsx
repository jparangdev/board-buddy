import {Link, Outlet, useNavigate} from 'react-router-dom';
import {useState} from 'react';
import {useAuth} from '@/hooks/useAuth';
import {authService} from '@/services';
import styles from './Layout.module.css';

export function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [isDeleting, setIsDeleting] = useState(false);

  const handleLogout = async () => {
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
    }
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
              <Link to="/games" className={styles.navLink}>
                Games
              </Link>
              <div className={styles.userSection}>
                <span className={styles.userTag}>{user.userTag}</span>
                <button 
                  onClick={handleDeleteAccount} 
                  className="btn btn-danger btn-sm"
                  style={{marginRight: '8px'}}
                  disabled={isDeleting}
                >
                  {isDeleting ? 'Deleting...' : 'Delete Account'}
                </button>
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
