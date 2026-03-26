export interface Invitation {
  id: number;
  groupId: number;
  groupName: string;
  inviterId: number;
  inviterNickname: string;
  inviteeId: number;
  createdAt: string;
}

export interface InvitationListResponse {
  invitations: Invitation[];
}
