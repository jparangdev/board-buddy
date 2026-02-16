import {useTranslation} from 'react-i18next';
import styles from './LanguageSwitcher.module.css';

export function LanguageSwitcher() {
  const {i18n} = useTranslation();

  const toggleLanguage = () => {
    const newLang = i18n.language === 'ko' ? 'en' : 'ko';
    i18n.changeLanguage(newLang);
    localStorage.setItem('language', newLang);
  };

  return (
    <button
      className={styles.languageSwitcher}
      onClick={toggleLanguage}
      aria-label="Toggle language"
    >
      {i18n.language === 'ko' ? 'EN' : '한글'}
    </button>
  );
}
