import {api} from './api';
import type {SocialAccountResponse, User} from '@/types';

interface SearchResponse {
  users: User[];
}

interface SocialAccountListResponse {
  accounts: SocialAccountResponse[];
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

  async getLinkedAccounts(): Promise<SocialAccountResponse[]> {
    const response = await api.get<SocialAccountListResponse>('/users/me/social-accounts');
    return response.accounts;
  },

  async linkAccount(provider: string, code: string, redirectUri: string): Promise<void> {
    await api.post(`/users/me/social-accounts/${provider}/link`, {code, redirectUri});
  },

  async unlinkAccount(provider: string): Promise<void> {
    await api.delete(`/users/me/social-accounts/${provider}`);
  },
};
