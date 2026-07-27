import { useCallback, useMemo, useState } from "react";
import type { ReactNode } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { clearStoredAuth, readStoredAuth, writeStoredAuth } from "../../api/tokenStorage";
import type { AuthResponse } from "../../types/api";
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
