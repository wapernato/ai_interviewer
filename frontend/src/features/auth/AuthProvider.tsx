import { useCallback, useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { apiClient } from "../../api/client";
import { clearStoredAuth, readStoredAuth, writeStoredAuth } from "../../api/tokenStorage";
import type { AuthResponse } from "../../types/api";
import { normalizeApiError } from "../../utils/apiError";
import { AuthContext, type AuthContextValue } from "./authContext";

type AuthProviderProps = {
  children: ReactNode;
};

export function AuthProvider({ children }: AuthProviderProps) {
  const queryClient = useQueryClient();
  const [auth, setAuth] = useState<AuthResponse | null>(() => readStoredAuth());

  const signIn = useCallback((nextAuth: AuthResponse) => {
    writeStoredAuth(nextAuth);
    setAuth(nextAuth);
  }, []);

  const signOut = useCallback(() => {
    clearStoredAuth();
    setAuth(null);
    queryClient.clear();
  }, [queryClient]);

  useEffect(() => {
    const interceptorId = apiClient.interceptors.response.use(
      (response) => response,
      (error: unknown) => {
        const apiError = normalizeApiError(error);

        if (apiError.status === 401) {
          signOut();
        }

        return Promise.reject(apiError);
      },
    );

    return () => apiClient.interceptors.response.eject(interceptorId);
  }, [signOut]);

  const value = useMemo<AuthContextValue>(
    () => ({
      auth,
      isAuthenticated: Boolean(auth?.token),
      role: auth?.role ?? null,
      signIn,
      signOut,
    }),
    [auth, signIn, signOut],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
