import { apiClient } from "./client";
import type {
  AuthResponse,
  LoginRequest,
  PasswordStrengthRequest,
  PasswordStrengthResult,
  RegisterRequest,
} from "../types/api";

export async function registerUser(request: RegisterRequest): Promise<AuthResponse> {
  const response = await apiClient.post<AuthResponse>("/auth/register", request);
  return response.data;
}

export async function loginUser(request: LoginRequest): Promise<AuthResponse> {
  const response = await apiClient.post<AuthResponse>("/auth/login", request);
  return response.data;
}

export async function getPasswordStrength(
  request: PasswordStrengthRequest,
  signal?: AbortSignal,
): Promise<PasswordStrengthResult> {
  const response = await apiClient.post<PasswordStrengthResult>("/auth/password-strength", request, { signal });
  return response.data;
}
