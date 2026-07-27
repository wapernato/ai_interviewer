import { apiClient } from "./client";
import type { AiProfileRequest, AiProfileResponse } from "../types/api";

export async function getAiProfiles(): Promise<AiProfileResponse[]> {
  const response = await apiClient.get<AiProfileResponse[]>("/ai-profiles");
  return response.data;
}

export async function createAiProfile(request: AiProfileRequest): Promise<AiProfileResponse> {
  const response = await apiClient.post<AiProfileResponse>("/admin/ai-profiles", request);
  return response.data;
}

export async function updateAiProfile(id: number, request: AiProfileRequest): Promise<AiProfileResponse> {
  const response = await apiClient.put<AiProfileResponse>(`/admin/ai-profiles/${id}`, request);
  return response.data;
}

export async function deleteAiProfile(id: number): Promise<void> {
  await apiClient.delete(`/admin/ai-profiles/${id}`);
}

export async function activateAiProfile(id: number): Promise<AiProfileResponse> {
  const response = await apiClient.put<AiProfileResponse>(`/admin/ai-profiles/${id}/activate`);
  return response.data;
}
