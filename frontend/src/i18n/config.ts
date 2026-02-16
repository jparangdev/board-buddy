import i18n from 'i18next';
import {initReactI18next} from 'react-i18next';
import en from './locales/en.json';
import ko from './locales/ko.json';

const resources = {
  en: {translation: en},
  ko: {translation: ko},
};

// Detect browser language or fallback to Korean
const browserLanguage = navigator.language.split('-')[0];
const defaultLanguage = ['ko', 'en'].includes(browserLanguage) ? browserLanguage : 'ko';

i18n
  .use(initReactI18next)
  .init({
    resources,
    lng: localStorage.getItem('language') || defaultLanguage,
    fallbackLng: 'ko',
    interpolation: {
      escapeValue: false,
    },
  });

export default i18n;
