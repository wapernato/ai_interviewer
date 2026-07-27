import { apiClient } from "./client";
import type { TopicRequest, TopicResponse } from "../types/api";

export async function getTopics(): Promise<TopicResponse[]> {
  const response = await apiClient.get<TopicResponse[]>("/topics");
  return response.data;
}

export async function getTopic(id: number): Promise<TopicResponse> {
  const response = await apiClient.get<TopicResponse>(`/topics/${id}`);
  return response.data;
}

export async function createTopic(request: TopicRequest): Promise<TopicResponse> {
  const response = await apiClient.post<TopicResponse>("/admin/topics", request);
  return response.data;
}

export async function updateTopic(id: number, request: TopicRequest): Promise<TopicResponse> {
  const response = await apiClient.put<TopicResponse>(`/admin/topics/${id}`, request);
  return response.data;
}

export async function deleteTopic(id: number): Promise<void> {
  await apiClient.delete(`/admin/topics/${id}`);
}
