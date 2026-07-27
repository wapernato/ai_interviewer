import { normalizeApiError } from "../utils/apiError";

export function useApiErrorMessage(error: unknown): string | null {
  if (!error) {
    return null;
  }

  return normalizeApiError(error).message;
}
