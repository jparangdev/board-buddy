export interface User {
  id: number;
  email: string;
  nickname: string;
  discriminator: string;
  userTag: string;
  provider: string;
}

export interface SocialAccountResponse {
  provider: string;
  linkedAt: string;
}
