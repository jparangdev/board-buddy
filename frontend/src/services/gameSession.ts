import {api} from './api';
import type {CreateSessionRequest, GameSession, GameSessionDetail, GroupStats, SessionListResponse} from '@/types';

export const gameSessionService = {
  async createSession(groupId: number, request: CreateSessionRequest): Promise<GameSession> {
    return api.post<GameSession>(`/groups/${groupId}/sessions`, request);
  },

  async getSessionsByGroup(groupId: number): Promise<GameSession[]> {
    const response = await api.get<SessionListResponse>(`/groups/${groupId}/sessions`);
    return response.sessions;
  },

  async getSessionDetail(groupId: number, sessionId: number): Promise<GameSessionDetail> {
    return api.get<GameSessionDetail>(`/groups/${groupId}/sessions/${sessionId}`);
  },

  async getGroupStats(groupId: number): Promise<GroupStats> {
    return api.get<GroupStats>(`/groups/${groupId}/stats`);
  },
};
