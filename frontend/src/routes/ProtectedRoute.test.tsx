import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { afterEach, describe, expect, it } from "vitest";
import { apiClient } from "../api/client";
import { writeStoredAuth } from "../api/tokenStorage";
import { AuthProvider } from "../features/auth/AuthProvider";
import { ApiError } from "../utils/apiError";
import { ProtectedRoute } from "./ProtectedRoute";

function PrivatePage() {
  function requestWithExpiredToken() {
    void apiClient.get("/private", {
      adapter: () => Promise.reject(new ApiError("Необходима авторизация.", 401, "UNAUTHORIZED")),
    }).catch(() => undefined);
  }

  return (
    <div>
      <span>Private page</span>
      <button onClick={requestWithExpiredToken} type="button">Request protected data</button>
    </div>
  );
}

function renderRoute() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
  });

  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={["/private"]}>
        <AuthProvider>
          <Routes>
            <Route element={<ProtectedRoute />}>
              <Route path="/private" element={<PrivatePage />} />
            </Route>
            <Route path="/login" element={<div>Login page</div>} />
          </Routes>
        </AuthProvider>
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

afterEach(() => {
  localStorage.clear();
});

describe("ProtectedRoute", () => {
  it("redirects anonymous user to login", () => {
    renderRoute();

    expect(screen.getByText("Login page")).toBeInTheDocument();
  });

  it("renders private content for authenticated user", () => {
    writeStoredAuth({
      id: 1,
      username: "admin",
      email: "admin@example.com",
      role: "ADMIN",
      token: "jwt",
    });

    renderRoute();

    expect(screen.getByText("Private page")).toBeInTheDocument();
  });

  it("redirects to login when the API rejects the stored token", async () => {
    const user = userEvent.setup();
    writeStoredAuth({
      id: 1,
      username: "admin",
      email: "admin@example.com",
      role: "ADMIN",
      token: "expired-jwt",
    });

    renderRoute();
    await user.click(screen.getByRole("button", { name: "Request protected data" }));

    expect(await screen.findByText("Login page")).toBeInTheDocument();
    expect(localStorage.getItem("ai_interviewer_auth")).toBeNull();
  });
});
