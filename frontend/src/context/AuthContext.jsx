import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { api, ApiError } from '../services/api';

const AuthContext = createContext(null);
const STORAGE_KEY = 'viptables-auth';

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => {
    try {
      return JSON.parse(sessionStorage.getItem(STORAGE_KEY)) || null;
    } catch {
      return null;
    }
  });
  const [loading, setLoading] = useState(Boolean(session));

  useEffect(() => {
    if (!session) return;
    api.me(session.auth)
      .then((user) => setSession((current) => ({ ...current, user })))
      .catch(() => {
        sessionStorage.removeItem(STORAGE_KEY);
        setSession(null);
      })
      .finally(() => setLoading(false));
  }, []);

  const value = useMemo(() => ({
    session,
    user: session?.user || null,
    loading,
    async login(email, password) {
      const auth = `Basic ${btoa(`${email}:${password}`)}`;
      const user = await api.me(auth);
      const next = { auth, user };
      sessionStorage.setItem(STORAGE_KEY, JSON.stringify(next));
      setSession(next);
      return user;
    },
    logout() {
      sessionStorage.removeItem(STORAGE_KEY);
      setSession(null);
    },
    async register(body) {
      return api.register(body);
    },
    isManager: session?.user?.role === 'MANAGER',
    isCustomer: session?.user?.role === 'CUSTOMER',
  }), [loading, session]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}

export { ApiError };
