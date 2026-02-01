import {api} from './api';
import type {Group, GroupListResponse, GroupMember, MemberListResponse} from '@/types';

export const groupService = {
  async create(name: string): Promise<Group> {
    return api.post<Group>('/groups', { name });
  },

  async getMyGroups(): Promise<Group[]> {
    const response = await api.get<GroupListResponse>('/groups');
    return response.groups;
  },

  async getById(id: number): Promise<Group> {
    return api.get<Group>(`/groups/${id}`);
  },

  async getMembers(groupId: number): Promise<GroupMember[]> {
    const response = await api.get<MemberListResponse>(`/groups/${groupId}/members`);
    return response.members;
  },

  async inviteMember(groupId: number, userTag: string): Promise<GroupMember> {
    return api.post<GroupMember>(`/groups/${groupId}/members`, { userTag });
  },
};
