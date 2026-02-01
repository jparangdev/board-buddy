import {api} from './api';
import type {User} from '@/types';

export const userService = {
  async getMe(): Promise<User> {
    return api.get<User>('/users/me');
  },

  async getById(id: number): Promise<User> {
    return api.get<User>(`/users/${id}`);
  },
};
