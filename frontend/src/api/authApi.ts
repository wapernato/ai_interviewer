import { apiClient } from "./client";
import type { AuthResponse, LoginRequest, RegisterRequest } from "../types/api";

export async function registerUser(request: RegisterRequest): Promise<AuthResponse> {
  const response = await apiClient.post<AuthResponse>("/auth/register", request);
  return response.data;
}

export async function loginUser(request: LoginRequest): Promise<AuthResponse> {
  const response = await apiClient.post<AuthResponse>("/auth/login", request);
  return response.data;
}
