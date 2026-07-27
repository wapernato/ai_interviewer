import { createContext } from "react";
import type { AuthResponse, UserRole } from "../../types/api";

export type AuthContextValue = {
  auth: AuthResponse | null;
  isAuthenticated: boolean;
  role: UserRole | null;
  signIn: (auth: AuthResponse) => void;
  signOut: () => void;
};

export const AuthContext = createContext<AuthContextValue | null>(null);
