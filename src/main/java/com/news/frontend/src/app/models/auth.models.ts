export interface UserInfo {
  id: number;
  name: string;
  email: string;
  role: 'USER' | 'ADMIN';
  avatarUrl?: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  user: UserInfo;
}
