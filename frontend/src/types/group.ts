export interface Group {
  id: number;
  name: string;
  ownerId: number;
  createdAt: string;
}

export interface GroupMember {
  id: number;
  nickname: string;
  discriminator: string;
  userTag: string;
  joinedAt: string | null;
  status?: 'ACTIVE' | 'PENDING';
}

export interface GroupListResponse {
  groups: Group[];
}

export interface MemberListResponse {
  members: GroupMember[];
}

export interface CreateGroupRequest {
  name: string;
  memberIds: number[];
}
