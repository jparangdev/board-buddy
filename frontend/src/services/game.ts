import {api} from './api';
import type {CreateGameRequest, Game, GameListResponse} from '@/types';

export const gameService = {
  async getGames(): Promise<Game[]> {
    const response = await api.get<GameListResponse>('/games');
    return response.games;
  },

  async getGameById(id: number): Promise<Game> {
    return api.get<Game>(`/games/${id}`);
  },

  async createGame(request: CreateGameRequest): Promise<Game> {
    return api.post<Game>('/games', request);
  },
};
