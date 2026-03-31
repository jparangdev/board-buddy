export interface Invitation {
  id: number;
  groupId: number;
  groupName: string;
  inviterId: number;
  inviterNickname: string;
  inviteeId: number;
  inviteeNickname?: string;
  status?: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  createdAt: string;
}

export interface InvitationListResponse {
  invitations: Invitation[];
}
