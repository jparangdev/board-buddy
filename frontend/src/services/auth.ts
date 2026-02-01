import {api, clearTokens, getRefreshToken, setTokens} from './api';
import type {TestLoginRequest, TokenResponse} from '@/types';

export const authService = {
  async testLogin(request: TestLoginRequest): Promise<TokenResponse> {
    const response = await api.post<TokenResponse>('/auth/test/login', request, { skipAuth: true });
    setTokens(response.accessToken, response.refreshToken);
    return response;
  },

  async refresh(): Promise<TokenResponse> {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }
    const response = await api.post<TokenResponse>('/auth/refresh', { refreshToken }, { skipAuth: true });
    setTokens(response.accessToken, response.refreshToken);
    return response;
  },

  async logout(): Promise<void> {
    const refreshToken = getRefreshToken();
    if (refreshToken) {
      await api.post('/auth/logout', { refreshToken });
    }
    clearTokens();
  },

  isAuthenticated(): boolean {
    return !!getRefreshToken();
  },
};
