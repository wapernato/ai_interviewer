import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { afterEach, describe, expect, it } from "vitest";
import { writeStoredAuth } from "../api/tokenStorage";
import { AuthProvider } from "../features/auth/AuthProvider";
import { ProtectedRoute } from "./ProtectedRoute";

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
              <Route path="/private" element={<div>Private page</div>} />
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
});
