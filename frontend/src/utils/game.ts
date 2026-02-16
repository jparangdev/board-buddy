import type {CustomGame, Game} from '@/types';

/**
 * 현재 언어에 맞는 게임 이름을 반환합니다.
 * 우선순위: 해당 언어 이름 > 기본 name
 */
export function getGameName(game: Game | CustomGame, locale: string): string {
  if (locale === 'ko' && game.nameKo) {
    return game.nameKo;
  }
  if (locale === 'en' && game.nameEn) {
    return game.nameEn;
  }
  return game.name;
}

/**
 * 검색을 위해 모든 가능한 게임 이름을 반환합니다.
 */
export function getAllGameNames(game: Game | CustomGame): string[] {
  const names = [game.name];
  if (game.nameKo) names.push(game.nameKo);
  if (game.nameEn) names.push(game.nameEn);
  return names;
}
