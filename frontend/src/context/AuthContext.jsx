import { createContext, useContext, useState } from 'react';
import api from '../api/client';

const AuthContext = createContext(null);

function lireUtilisateur() {
  try {
    return JSON.parse(localStorage.getItem('user'));
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(lireUtilisateur);

  const enregistrer = (data) => {
    const { token, ...infos } = data;
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(infos));
    setUser(infos);
  };

  const login = async (email, motDePasse) => {
    const { data } = await api.post('/auth/login', { email, motDePasse });
    enregistrer(data);
    return data;
  };

  const register = async (formulaire) => {
    const { data } = await api.post('/auth/register', formulaire);
    enregistrer(data);
    return data;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
