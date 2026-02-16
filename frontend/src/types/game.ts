export interface Game {
  id: number;
  name: string;
  nameKo?: string;
  nameEn?: string;
  minPlayers: number;
  maxPlayers: number;
  scoreStrategy: string;
  createdAt: string;
}

export interface GameListResponse {
  games: Game[];
}

export interface CreateGameRequest {
  name: string;
  minPlayers: number;
  maxPlayers: number;
  scoreStrategy: string;
}

export interface CustomGame {
  id: number;
  groupId: number;
  name: string;
  nameKo?: string;
  nameEn?: string;
  minPlayers: number;
  maxPlayers: number;
  scoreStrategy: string;
  createdAt: string;
}

export interface CustomGameListResponse {
  customGames: CustomGame[];
}

export interface CreateCustomGameRequest {
  name: string;
  minPlayers: number;
  maxPlayers: number;
  scoreStrategy: string;
}

export interface GameSession {
  id: number;
  groupId: number;
  gameId: number | null;
  customGameId: number | null;
  gameName: string;
  playedAt: string;
  createdAt: string;
}

export interface GameResult {
  userId: number;
  nickname: string;
  userTag: string;
  score: number | null;
  rank: number;
}

export interface GameSessionDetail {
  id: number;
  groupId: number;
  gameId: number | null;
  customGameId: number | null;
  gameName: string;
  scoreStrategy: string;
  playedAt: string;
  createdAt: string;
  results: GameResult[];
}

export interface SessionListResponse {
  sessions: GameSession[];
}

export interface CreateSessionRequest {
  gameId?: number;
  customGameId?: number;
  playedAt: string;
  results: ResultInput[];
}

export interface ResultInput {
  userId: number;
  score: number | null;
  won?: boolean;
}
