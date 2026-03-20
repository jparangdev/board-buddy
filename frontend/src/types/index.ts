export * from './auth';
export * from './user';
export * from './group';
export * from './game';

export interface ApiError {
  error: string;
  message: string;
  status: number;
  fieldErrors?: Record<string, string>;
}
