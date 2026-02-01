/* eslint-disable react-refresh/only-export-components */
import {createContext, type ReactNode, useContext, useEffect, useState} from 'react';
import type {User} from '@/types';
import {authService, userService} from '@/services';

interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (email: string, nickname?: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const refreshUser = async () => {
    try {
      const userData = await userService.getMe();
      setUser(userData);
    } catch {
      setUser(null);
    }
  };

  useEffect(() => {
    const initAuth = async () => {
      if (authService.isAuthenticated()) {
        await refreshUser();
      }
      setIsLoading(false);
    };
    initAuth();
  }, []);

  const login = async (email: string, nickname?: string) => {
    await authService.testLogin({ email, nickname });
    await refreshUser();
  };

  const logout = async () => {
    await authService.logout();
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isLoading,
        isAuthenticated: !!user,
        login,
        logout,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
