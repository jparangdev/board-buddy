import {type FormEvent, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {useAuth} from '@/hooks/useAuth';
import styles from './LoginPage.module.css';

export function LoginPage() {
  const [email, setEmail] = useState('');
  const [nickname, setNickname] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      await login(email, nickname || undefined);
      navigate('/groups');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <div className={styles.header}>
          <span className={styles.icon}>&#x1F3B2;</span>
          <h1>Board Buddy</h1>
          <p className={styles.subtitle}>Find your game night crew</p>
        </div>

        <form onSubmit={handleSubmit} className={styles.form}>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              className="input"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Enter your email"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="nickname">Nickname (optional)</label>
            <input
              id="nickname"
              type="text"
              className="input"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              placeholder="Choose a nickname"
            />
          </div>

          {error && <p className={styles.error}>{error}</p>}

          <button type="submit" className="btn btn-primary" disabled={isLoading}>
            {isLoading ? 'Entering...' : 'Enter the Game Room'}
          </button>
        </form>

        <div className={styles.footer}>
          <p className={styles.hint}>
            &#x2139; This is a test login for development
          </p>
        </div>
      </div>
    </div>
  );
}
