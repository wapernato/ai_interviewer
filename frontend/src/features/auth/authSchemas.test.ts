import { describe, expect, it } from "vitest";
import { loginSchema, registerSchema } from "./authSchemas";

describe("auth schemas", () => {
  it("rejects invalid login data", () => {
    const result = loginSchema.safeParse({ email: "wrong", password: "" });

    expect(result.success).toBe(false);
  });

  it("accepts valid register data", () => {
    const result = registerSchema.safeParse({
      username: "ivan",
      email: "ivan@example.com",
      password: "password123",
    });

    expect(result.success).toBe(true);
  });
});
