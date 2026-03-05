import {type FormEvent, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import {authService} from '@/services';
import type {ApiError} from '@/types';
import styles from './RegisterPage.module.css';

export function RegisterPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const { t } = useTranslation();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setFieldErrors({});

    if (password !== confirmPassword) {
      setFieldErrors({ confirmPassword: t('auth.passwordMismatch') });
      return;
    }

    setIsLoading(true);

    try {
      await authService.register({ email, password, nickname });
      navigate('/login');
    } catch (err) {
      const apiErr = err as ApiError;
      if (apiErr?.fieldErrors) {
        setFieldErrors(apiErr.fieldErrors);
      } else {
        const msg = err instanceof Error ? err.message : apiErr?.message;
        setError(msg ?? t('auth.registerFailed'));
      }
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
          <p className={styles.subtitle}>{t('auth.registerTitle')}</p>
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
            {fieldErrors.email && <p className={styles.fieldError}>{fieldErrors.email}</p>}
          </div>

          <div className="form-group">
            <label htmlFor="nickname">{t('auth.nickname')}</label>
            <input
              id="nickname"
              type="text"
              className="input"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              placeholder={t('auth.nicknamePlaceholder')}
              required
            />
            {fieldErrors.nickname && <p className={styles.fieldError}>{fieldErrors.nickname}</p>}
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
            {fieldErrors.password && <p className={styles.fieldError}>{fieldErrors.password}</p>}
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">{t('auth.confirmPassword')}</label>
            <input
              id="confirmPassword"
              type="password"
              className="input"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder={t('auth.confirmPasswordPlaceholder')}
              required
            />
            {fieldErrors.confirmPassword && <p className={styles.fieldError}>{fieldErrors.confirmPassword}</p>}
          </div>

          {error && <p className={styles.error}>{error}</p>}

          <button type="submit" className="btn btn-primary" disabled={isLoading}>
            {isLoading ? t('auth.registering') : t('auth.registerButton')}
          </button>
        </form>

        <div className={styles.footer}>
          <p className={styles.hint}>
            <Link to="/login">{t('auth.goToLogin')}</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
