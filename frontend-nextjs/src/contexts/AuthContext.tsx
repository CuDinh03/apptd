"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode
} from "react";

import type { AuthTokenResponse } from "@/lib/api";
import { clearAuthSession, loadAuthState, saveAuthSession, type SessionUser } from "@/lib/auth-session";

type AuthContextValue = {
  token: string;
  user: SessionUser | null;
  /** true sau khi đã đọc sessionStorage trên client (tránh flash sai trạng thái). */
  ready: boolean;
  signIn: (auth: AuthTokenResponse) => void;
  signOut: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState("");
  const [user, setUser] = useState<SessionUser | null>(null);
  const [ready, setReady] = useState(false);

  useEffect(() => {
    const s = loadAuthState();
    setToken(s.token);
    setUser(s.user);
    setReady(true);
  }, []);

  const signIn = useCallback((auth: AuthTokenResponse) => {
    saveAuthSession(auth);
    const s = loadAuthState();
    setToken(s.token);
    setUser(s.user);
  }, []);

  const signOut = useCallback(() => {
    clearAuthSession();
    setToken("");
    setUser(null);
  }, []);

  const value = useMemo(
    () => ({ token, user, ready, signIn, signOut }),
    [token, user, ready, signIn, signOut]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return ctx;
}
