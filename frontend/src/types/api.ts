export type UserRole = "USER" | "ADMIN";

export type ErrorResponse = {
  status: number;
  error: string;
  message: string;
};

export type ValidationErrorResponse = ErrorResponse & {
  validationErrors: Record<string, string>;
};

export type ApiErrorPayload = ErrorResponse | ValidationErrorResponse;

export type AuthResponse = {
  id: number;
  username: string;
  email: string;
  role: UserRole;
  token: string;
};

export type RegisterRequest = {
  username: string;
  email: string;
  password: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type PasswordStrengthLevel = "WEAK" | "MEDIUM" | "STRONG";

export type PasswordStrengthRequest = {
  password: string;
};

export type PasswordStrengthResult = {
  level: PasswordStrengthLevel;
  suggestions: string[];
};

export type UserResponse = {
  id: number;
  username: string;
  email: string;
  role: UserRole;
  enabled: boolean;
  createdAt: string;
};

export type UpdateUserRequest = {
  username: string;
};

export type UserStatisticsResponse = {
  totalQuestions: number;
  totalAnswers: number;
  unansweredQuestions: number;
  completionRate: number;
};

export type UpdateUserRoleRequest = {
  role: UserRole;
};

export type TopicResponse = {
  id: number;
  name: string;
};

export type TopicRequest = {
  name: string;
};

export type AiProfileResponse = {
  id: number;
  mode: string;
  descriptionMode?: string | null;
  instructionMode: string;
  modelName: string;
  language: "ru" | "en";
  answerStyle?: string | null;
  difficulty: "easy" | "medium" | "hard";
  feedbackMode: "short" | "detailed" | "strict";
  hintMode: boolean;
  active: boolean;
  temperature: number;
  maxTokens: number;
};

export type AiProfileRequest = {
  mode: string;
  descriptionMode?: string;
  instructionMode: string;
  modelName: string;
  language: "ru" | "en";
  answerStyle?: string;
  difficulty: "easy" | "medium" | "hard";
  feedbackMode: "short" | "detailed" | "strict";
  hintMode: boolean;
  active: boolean;
  temperature: number;
  maxTokens: number;
};

export type QuestionRequest = {
  topic: string;
};

export type InterviewQuestionResult = {
  questionId: number;
  userId: number;
  topicId: number;
  aiProfileId: number;
  topicName: string;
  questionText: string;
  aiMode: string;
  difficulty: string;
};

export type AnswerRequest = {
  userId: number;
  questionId: number;
  textAnswer: string;
};

export type InterviewAnswerResult = {
  userId: number;
  questionId: number;
  answerId: number;
  questionText: string;
  userAnswerText: string;
  feedback: string;
};

export type UserHistoryItem = {
  questionId: number;
  username: string;
  topicName: string | null;
  textQuestion: string;
  answerText: string | null;
  modelName: string | null;
};
