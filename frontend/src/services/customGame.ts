import {api} from './api';
import type {CreateCustomGameRequest, CustomGame, CustomGameListResponse} from '@/types';

export const customGameService = {
  async getCustomGames(groupId: number): Promise<CustomGame[]> {
    const response = await api.get<CustomGameListResponse>(`/groups/${groupId}/custom-games`);
    return response.customGames;
  },

  async createCustomGame(groupId: number, request: CreateCustomGameRequest): Promise<CustomGame> {
    return api.post<CustomGame>(`/groups/${groupId}/custom-games`, request);
  },
};
