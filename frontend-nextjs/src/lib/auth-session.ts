import type { AuthTokenResponse } from "@/lib/api";

/** Token cũ trên localStorage (trước khi chuyển sang phiên tab). */
const LEGACY_JWT_LOCAL_KEY = "jwt_token";

/** JWT cho API — lưu sessionStorage: hết khi đóng tab/trình duyệt hoặc đăng xuất. */
export const SESSION_TOKEN_KEY = "apptd_access_token";

export const SESSION_USER_KEY = "apptd_session_user";

export type SessionUser = {
  username: string;
  role: string;
  userId: number;
};

function getSessionStorage(): Storage | null {
  if (typeof window === "undefined") return null;
  try {
    return window.sessionStorage;
  } catch {
    return null;
  }
}

/** Một lần: chuyển token từ localStorage cũ sang sessionStorage. */
export function migrateLegacyJwtToSession(): void {
  if (typeof window === "undefined") return;
  try {
    const legacy = window.localStorage.getItem(LEGACY_JWT_LOCAL_KEY);
    if (!legacy?.trim()) return;
    const ss = window.sessionStorage;
    if (!ss.getItem(SESSION_TOKEN_KEY)) {
      ss.setItem(SESSION_TOKEN_KEY, legacy.trim());
    }
    window.localStorage.removeItem(LEGACY_JWT_LOCAL_KEY);
  } catch {
    // private mode / disabled storage
  }
}

export function readJwtToken(): string {
  if (typeof window === "undefined") return "";
  migrateLegacyJwtToSession();
  try {
    return (window.sessionStorage.getItem(SESSION_TOKEN_KEY) ?? "").trim();
  } catch {
    return "";
  }
}

export function loadAuthState(): { token: string; user: SessionUser | null } {
  if (typeof window === "undefined") {
    return { token: "", user: null };
  }
  migrateLegacyJwtToSession();
  try {
    const token = (window.sessionStorage.getItem(SESSION_TOKEN_KEY) ?? "").trim();
    const raw = window.sessionStorage.getItem(SESSION_USER_KEY);
    let user: SessionUser | null = null;
    if (raw) {
      try {
        const parsed = JSON.parse(raw) as Partial<SessionUser>;
        if (
          parsed &&
          typeof parsed.username === "string" &&
          typeof parsed.role === "string" &&
          typeof parsed.userId === "number"
        ) {
          user = { username: parsed.username, role: parsed.role, userId: parsed.userId };
        }
      } catch {
        user = null;
      }
    }
    return { token, user };
  } catch {
    return { token: "", user: null };
  }
}

export function saveAuthSession(auth: AuthTokenResponse): void {
  const ss = getSessionStorage();
  if (!ss) return;
  try {
    migrateLegacyJwtToSession();
    try {
      window.localStorage.removeItem(LEGACY_JWT_LOCAL_KEY);
    } catch {
      // ignore
    }
    ss.setItem(SESSION_TOKEN_KEY, auth.accessToken);
    const user: SessionUser = {
      username: auth.username,
      role: auth.role,
      userId: auth.userId
    };
    ss.setItem(SESSION_USER_KEY, JSON.stringify(user));
  } catch {
    // ignore
  }
}

export function clearAuthSession(): void {
  const ss = getSessionStorage();
  if (!ss) return;
  try {
    ss.removeItem(SESSION_TOKEN_KEY);
    ss.removeItem(SESSION_USER_KEY);
  } catch {
    // ignore
  }
  try {
    if (typeof window !== "undefined") {
      window.localStorage.removeItem(LEGACY_JWT_LOCAL_KEY);
    }
  } catch {
    // ignore
  }
}
