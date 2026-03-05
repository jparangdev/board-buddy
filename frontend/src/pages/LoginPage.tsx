import {type FormEvent, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import {useAuth} from '@/hooks/useAuth';
import styles from './LoginPage.module.css';

export function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const { t } = useTranslation();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      await login(email, password);
      navigate('/groups');
    } catch (err) {
      const msg = err instanceof Error ? err.message : (err as { message?: string })?.message;
      setError(msg ?? t('auth.loginFailed'));
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
          <p className={styles.subtitle}>{t('auth.loginTitle')}</p>
        </div>

        <form onSubmit={handleSubmit} className={styles.form}>
          <div className="form-group">
            <label htmlFor="email">{t('auth.email')}</label>
            <input
              id="email"
              type="email"
              className="input"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder={t('auth.emailPlaceholder')}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">{t('auth.password')}</label>
            <input
              id="password"
              type="password"
              className="input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder={t('auth.passwordPlaceholder')}
              required
            />
          </div>

          {error && <p className={styles.error}>{error}</p>}

          <button type="submit" className="btn btn-primary" disabled={isLoading}>
            {isLoading ? t('auth.loggingIn') : t('auth.loginButton')}
          </button>
        </form>

        <div className={styles.footer}>
          <p className={styles.hint}>
            <Link to="/register">{t('auth.goToRegister')}</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
