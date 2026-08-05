import { apiClient } from "./client";
import type {
  UpdateUserRequest,
  UserHistoryItem,
  UserResponse,
  UserStatisticsResponse,
} from "../types/api";

export async function getMe(): Promise<UserResponse> {
  const response = await apiClient.get<UserResponse>("/me");
  return response.data;
}

export async function updateMe(request: UpdateUserRequest): Promise<UserResponse> {
  const response = await apiClient.put<UserResponse>("/me", request);
  return response.data;
}

export async function deleteMe(): Promise<void> {
  await apiClient.delete("/me");
}

export async function getMyHistory(): Promise<UserHistoryItem[]> {
  const response = await apiClient.get<UserHistoryItem[]>("/me/interview-history");
  return response.data;
}

export async function getMyStatistics(): Promise<UserStatisticsResponse> {
  const response = await apiClient.get<UserStatisticsResponse>("/me/statistics");
  return response.data;
}
