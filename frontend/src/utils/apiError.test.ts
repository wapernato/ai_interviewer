import axios from "axios";
import { describe, expect, it } from "vitest";
import { normalizeApiError } from "./apiError";

describe("normalizeApiError", () => {
  it("uses backend error response message", () => {
    const error = new axios.AxiosError(
      "Request failed",
      "ERR_BAD_REQUEST",
      undefined,
      undefined,
      {
        config: { headers: new axios.AxiosHeaders() },
        data: { status: 400, error: "BAD_REQUEST", message: "Некорректные данные" },
        headers: {},
        status: 400,
        statusText: "Bad Request",
      },
    );

    expect(normalizeApiError(error).message).toBe("Некорректные данные");
  });

  it("maps forbidden response to readable message", () => {
    const error = new axios.AxiosError(
      "Request failed",
      "ERR_BAD_REQUEST",
      undefined,
      undefined,
      {
        config: { headers: new axios.AxiosHeaders() },
        data: undefined,
        headers: {},
        status: 403,
        statusText: "Forbidden",
      },
    );

    expect(normalizeApiError(error).message).toBe("Недостаточно прав для выполнения действия.");
  });
});
