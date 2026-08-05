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
      password: "StrongPass1!",
    });

    expect(result.success).toBe(true);
  });

  it("rejects a password that does not satisfy the backend policy", () => {
    const result = registerSchema.safeParse({
      username: "ivan",
      email: "ivan@example.com",
      password: "password123",
    });

    expect(result.success).toBe(false);
  });

  it("rejects a password longer than bcrypt supports", () => {
    const result = registerSchema.safeParse({
      username: "ivan",
      email: "ivan@example.com",
      password: `Aa1!${"x".repeat(69)}`,
    });

    expect(result.success).toBe(false);
  });
});
