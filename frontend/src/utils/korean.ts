/**
 * Korean character utilities for search functionality
 */

const CHOSUNG_LIST = [
  'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
  'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
];

const HANGUL_START = 0xAC00;
const HANGUL_END = 0xD7A3;
const CHOSUNG_BASE = 588;

/**
 * Extract chosung (initial consonant) from a Korean character
 */
function getChosung(char: string): string {
  const code = char.charCodeAt(0);

  if (code >= HANGUL_START && code <= HANGUL_END) {
    const chosungIndex = Math.floor((code - HANGUL_START) / CHOSUNG_BASE);
    return CHOSUNG_LIST[chosungIndex];
  }

  return char;
}

/**
 * Extract all chosung from a Korean string
 */
export function extractChosung(text: string): string {
  return text.split('').map(getChosung).join('');
}

/**
 * Check if search query matches text (supports Korean chosung search)
 */
export function matchesSearch(text: string, query: string): boolean {
  if (!query) return true;

  const lowerText = text.toLowerCase();
  const lowerQuery = query.toLowerCase();

  // Direct match
  if (lowerText.includes(lowerQuery)) {
    return true;
  }

  // Chosung match
  const textChosung = extractChosung(text);
  const queryChosung = extractChosung(query);

  return textChosung.includes(queryChosung);
}

/**
 * Sort array alphabetically, handling Korean characters
 */
export function sortAlphabetically<T>(
  array: T[],
  keyFn: (item: T) => string
): T[] {
  return [...array].sort((a, b) => {
    const aKey = keyFn(a);
    const bKey = keyFn(b);
    return aKey.localeCompare(bKey, 'en');
  });
}
