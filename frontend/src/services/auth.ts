import {api, clearTokens, getRefreshToken, setTokens} from './api';
import type {LoginRequest, RegisterRequest, TokenResponse} from '@/types';

export const authService = {
  async login(request: LoginRequest): Promise<TokenResponse> {
    const response = await api.post<TokenResponse>(
      '/auth/login',
      request,
      { skipAuth: true, suppressErrorEvent: true, suppressAuthError: true },
    );
    setTokens(response.accessToken, response.refreshToken);
    return response;
  },

  async register(request: RegisterRequest): Promise<void> {
    await api.post<void>(
      '/auth/register',
      request,
      { skipAuth: true, suppressErrorEvent: true, suppressAuthError: true },
    );
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

  async deleteAccount(): Promise<void> {
    await api.delete('/users/me');
  },

  async logout(): Promise<void> {
    const refreshToken = getRefreshToken();
    if (refreshToken) {
      await api.post('/auth/logout', { refreshToken });
    }
    clearTokens();
  },

  clearSession(): void {
    clearTokens();
  },

  isAuthenticated(): boolean {
    return !!getRefreshToken();
  },
};
