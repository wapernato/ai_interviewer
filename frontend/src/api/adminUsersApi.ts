import { apiClient } from "./client";
import type { UpdateUserRoleRequest, UserResponse } from "../types/api";

export async function getAdminUsers(): Promise<UserResponse[]> {
  const response = await apiClient.get<UserResponse[]>("/admin/users");
  return response.data;
}

export async function updateAdminUserRole(
  userId: number,
  request: UpdateUserRoleRequest,
): Promise<UserResponse> {
  const response = await apiClient.put<UserResponse>(`/admin/users/${userId}/role`, request);
  return response.data;
}
