import { apiClient } from "./client";
import type {
  AnswerRequest,
  InterviewAnswerResult,
  InterviewQuestionResult,
  QuestionRequest,
} from "../types/api";

export async function generateQuestion(request: QuestionRequest): Promise<InterviewQuestionResult> {
  const response = await apiClient.post<InterviewQuestionResult>("/interview/question", request);
  return response.data;
}

export async function submitAnswer(request: AnswerRequest): Promise<InterviewAnswerResult> {
  const response = await apiClient.post<InterviewAnswerResult>("/interview/answer", request);
  return response.data;
}
