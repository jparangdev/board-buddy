import { useTranslation } from 'react-i18next';
import type { ApiError } from '@/types';
import styles from './ServerErrorModal.module.css';

interface Props {
  error: ApiError;
  onClose: () => void;
}

export function ServerErrorModal({ error, onClose }: Props) {
  const { t } = useTranslation();

  return (
    <div className={styles.overlay} role="dialog" aria-modal="true">
      <div className={styles.dialog}>
        <h2 className={styles.title}>{t('error.serverErrorTitle')}</h2>
        <p className={styles.message}>{t('error.serverErrorMessage')}</p>
        <div className={styles.codeBox}>
          <span className={styles.codeLabel}>{t('error.errorCode')}</span>
          <code className={styles.code}>{error.error}</code>
        </div>
        <p className={styles.contact}>{t('error.contactAdmin')}</p>
        <button className="btn btn-secondary" onClick={onClose}>
          {t('common.close')}
        </button>
      </div>
    </div>
  );
}
