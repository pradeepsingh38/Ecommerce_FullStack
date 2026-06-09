import { useCallback, useEffect, useState } from "react";
import { getCurrentUser, logoutUser } from "../api/authApi";
import { AuthContext } from "./AuthContextObject";

const SESSION_EXPIRED_MESSAGE = "Your session expired. Please login again.";

const getTokenExpirationTime = (token) => {
  try {
    const encodedPayload = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
    const paddedPayload = encodedPayload.padEnd(
      encodedPayload.length + ((4 - (encodedPayload.length % 4)) % 4),
      "="
    );
    const payload = JSON.parse(atob(paddedPayload));
    return payload.exp ? payload.exp * 1000 : null;
  } catch {
    return null;
  }
};

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(() => Boolean(localStorage.getItem("token")));

  const clearSession = useCallback(({ notify = false } = {}) => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    if (notify) {
      sessionStorage.setItem("authMessage", SESSION_EXPIRED_MESSAGE);
    }
    setUser(null);
  }, []);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      return;
    }

    getCurrentUser()
      .then((res) => {
        setUser(res.data);
      })
      .catch(() => {
        clearSession({ notify: true });
      })
      .finally(() => {
        setLoading(false);
      });
  }, [clearSession]);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      return undefined;
    }

    const expiresAt = getTokenExpirationTime(token);
    if (!expiresAt) {
      return undefined;
    }

    const timeoutId = window.setTimeout(() => {
      clearSession({ notify: true });
    }, Math.max(expiresAt - Date.now(), 0));

    return () => window.clearTimeout(timeoutId);
  }, [clearSession, user]);

  useEffect(() => {
    const handleSessionExpired = () => clearSession({ notify: true });
    window.addEventListener("auth:session-expired", handleSessionExpired);

    return () => {
      window.removeEventListener("auth:session-expired", handleSessionExpired);
    };
  }, [clearSession]);

  const login = (userData, token) => {
    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(userData));
    sessionStorage.removeItem("authMessage");
    setUser(userData);
  };

  const updateUser = (userData, token) => {
    if (token) {
      localStorage.setItem("token", token);
    }
    localStorage.setItem("user", JSON.stringify(userData));
    setUser(userData);
  };

  const logout = async () => {
    try {
      if (localStorage.getItem("token")) {
        await logoutUser();
      }
    } catch {
      // Local cleanup is still the source of truth for this stateless JWT logout.
    }
    clearSession();
  };

  if (loading) return null;

  return (
    <AuthContext.Provider value={{ user, login, logout, updateUser }}>
      {children}
    </AuthContext.Provider>
  );
}
