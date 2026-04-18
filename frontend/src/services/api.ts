import type {ApiError} from '@/types';

const API_BASE_URL = '/api/v1';

interface RequestOptions extends RequestInit {
  params?: Record<string, string>;
  skipAuth?: boolean;
  suppressErrorEvent?: boolean;
  suppressAuthError?: boolean;
}

function getAccessToken(): string | null {
  return localStorage.getItem('accessToken');
}

export function setTokens(accessToken: string, refreshToken: string): void {
  localStorage.setItem('accessToken', accessToken);
  localStorage.setItem('refreshToken', refreshToken);
}

export function clearTokens(): void {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
}

export function getRefreshToken(): string | null {
  return localStorage.getItem('refreshToken');
}

async function request<T>(endpoint: string, options: RequestOptions = {}): Promise<T> {
  const { params, skipAuth, suppressErrorEvent, suppressAuthError, ...init } = options;

  let url = `${API_BASE_URL}${endpoint}`;
  if (params) {
    const searchParams = new URLSearchParams(params);
    url += `?${searchParams.toString()}`;
  }

  const lang = localStorage.getItem('language') || 'ko';
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    'Accept-Language': lang,
    ...(init.headers as Record<string, string>),
  };

  if (!skipAuth) {
    const token = getAccessToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
  }

  const response = await fetch(url, {
    ...init,
    headers,
  });

  if (!response.ok) {
    const body = await response.json().catch(() => ({
      error: 'UNKNOWN_ERROR',
      message: `HTTP error! status: ${response.status}`,
    }));
    const errorBody: ApiError = { ...body, status: response.status };

    if (response.status === 401 || response.status === 403) {
      if (!suppressAuthError) {
        clearTokens();
        if (!suppressErrorEvent) {
          window.dispatchEvent(new CustomEvent('boardbuddy:auth-error', { detail: errorBody }));
        }
      }
    } else if (!suppressErrorEvent) {
      if (response.status >= 500) {
        window.dispatchEvent(new CustomEvent('boardbuddy:server-error', { detail: errorBody }));
      } else {
        window.dispatchEvent(new CustomEvent('boardbuddy:client-error', { detail: errorBody }));
      }
    }

    throw errorBody;
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const text = await response.text();
  return text ? JSON.parse(text) : undefined as T;
}

export const api = {
  get: <T>(endpoint: string, options?: RequestOptions) =>
    request<T>(endpoint, { ...options, method: 'GET' }),

  post: <T>(endpoint: string, body?: unknown, options?: RequestOptions) =>
    request<T>(endpoint, { ...options, method: 'POST', body: JSON.stringify(body) }),

  put: <T>(endpoint: string, body?: unknown, options?: RequestOptions) =>
    request<T>(endpoint, { ...options, method: 'PUT', body: JSON.stringify(body) }),

  patch: <T>(endpoint: string, body?: unknown, options?: RequestOptions) =>
    request<T>(endpoint, { ...options, method: 'PATCH', body: JSON.stringify(body) }),

  delete: <T>(endpoint: string, options?: RequestOptions) =>
    request<T>(endpoint, { ...options, method: 'DELETE' }),
};
