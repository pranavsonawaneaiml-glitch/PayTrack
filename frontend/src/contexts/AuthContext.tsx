import { createContext, useContext, useState, ReactNode } from 'react';
import api from '../services/api';

interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: 'ADMIN' | 'MANAGER' | 'EMPLOYEE';
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (email: string, password: string) => Promise<void>;
  register: (data: any) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));

  if (token) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  }

  const login = async (email: string, password: string) => {
    const response = await api.post('/auth/login', { email, password });
    const { token: jwtToken, ...userData } = response.data;
    localStorage.setItem('token', jwtToken);
    setToken(jwtToken);
    setUser(userData);
    api.defaults.headers.common['Authorization'] = `Bearer ${jwtToken}`;
  };

  const register = async (data: any) => {
    const response = await api.post('/auth/register', data);
    const { token: jwtToken, ...userData } = response.data;
    localStorage.setItem('token', jwtToken);
    setToken(jwtToken);
    setUser(userData);
    api.defaults.headers.common['Authorization'] = `Bearer ${jwtToken}`;
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
    delete api.defaults.headers.common['Authorization'];
  };

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};
