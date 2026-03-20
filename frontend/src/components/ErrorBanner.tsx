import { useEffect } from 'react';
import type { ApiError } from '@/types';
import styles from './ErrorBanner.module.css';

interface Props {
  error: ApiError;
  onClose: () => void;
}

const AUTO_DISMISS_MS = 5000;

export function ErrorBanner({ error, onClose }: Props) {
  useEffect(() => {
    const timer = setTimeout(onClose, AUTO_DISMISS_MS);
    return () => clearTimeout(timer);
  }, [onClose]);

  return (
    <div className={styles.banner} role="alert">
      <span className={styles.message}>{error.message}</span>
      <button className={styles.closeBtn} onClick={onClose} aria-label="close">✕</button>
    </div>
  );
}
