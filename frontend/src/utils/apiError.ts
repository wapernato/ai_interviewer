import axios from "axios";
import type { ApiErrorPayload, ValidationErrorResponse } from "../types/api";

export class ApiError extends Error {
  status?: number;
  code?: string;
  validationErrors?: Record<string, string>;

  constructor(message: string, status?: number, code?: string, validationErrors?: Record<string, string>) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.code = code;
    this.validationErrors = validationErrors;
  }
}

function hasValidationErrors(payload: ApiErrorPayload): payload is ValidationErrorResponse {
  return "validationErrors" in payload && payload.validationErrors !== undefined;
}

export function normalizeApiError(error: unknown): ApiError {
  if (error instanceof ApiError) {
    return error;
  }

  if (axios.isAxiosError<ApiErrorPayload>(error)) {
    const status = error.response?.status;
    const payload = error.response?.data;

    if (payload && typeof payload === "object" && "message" in payload) {
      return new ApiError(
        payload.message || "Ошибка API.",
        payload.status || status,
        payload.error,
        hasValidationErrors(payload) ? payload.validationErrors : undefined,
      );
    }

    if (status === 401) {
      return new ApiError("Необходима авторизация.", 401, "UNAUTHORIZED");
    }

    if (status === 403) {
      return new ApiError("Недостаточно прав для выполнения действия.", 403, "FORBIDDEN");
    }

    if (status === 404) {
      return new ApiError("Ресурс не найден.", 404, "NOT_FOUND");
    }

    if (status && status >= 500) {
      return new ApiError("Сервер временно недоступен.", status, "SERVER_ERROR");
    }

    if (error.message) {
      return new ApiError(error.message, status);
    }
  }

  if (error instanceof Error) {
    return new ApiError(error.message);
  }

  return new ApiError("Неизвестная ошибка.");
}

export function getFieldError(error: unknown, field: string): string | undefined {
  const apiError = normalizeApiError(error);
  return apiError.validationErrors?.[field];
}
