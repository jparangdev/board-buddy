import {api} from './api';
import type {Invitation, InvitationListResponse} from '@/types';

export const invitationService = {
  async inviteUser(groupId: number, inviteeId: number): Promise<void> {
    return api.post(`/groups/${groupId}/invitations`, { inviteeId });
  },

  async getPendingInvitations(): Promise<Invitation[]> {
    const response = await api.get<InvitationListResponse>('/invitations/pending');
    return response.invitations;
  },

  async accept(id: number): Promise<void> {
    return api.post(`/invitations/${id}/accept`);
  },

  async reject(id: number): Promise<void> {
    return api.post(`/invitations/${id}/reject`);
  },
};
