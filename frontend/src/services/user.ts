import {api} from './api';
import type {User} from '@/types';

interface SearchResponse {
  users: User[];
}

export const userService = {
  async getMe(): Promise<User> {
    return api.get<User>('/users/me');
  },

  async getById(id: number): Promise<User> {
    return api.get<User>(`/users/${id}`);
  },

  async searchUsers(keyword: string): Promise<User[]> {
    const response = await api.get<SearchResponse>('/users/search', {
      params: { keyword },
    });
    return response.users;
  },

  async updateNickname(nickname: string): Promise<User> {
    return api.patch<User>('/users/me/nickname', { nickname });
  },
};
