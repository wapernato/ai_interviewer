import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Save, ShieldCheck, UserRoundCheck } from "lucide-react";
import { getAdminUsers, updateAdminUserRole } from "../api/adminUsersApi";
import { Alert } from "../components/Alert";
import { EmptyState } from "../components/EmptyState";
import { LoadingState } from "../components/LoadingState";
import { PageHeader } from "../components/PageHeader";
import { useAuth } from "../features/auth/useAuth";
import { useApiErrorMessage } from "../hooks/useApiErrorMessage";
import type { UserResponse, UserRole } from "../types/api";

type RoleDrafts = Record<number, UserRole>;

function formatCreatedAt(value: string) {
  return new Intl.DateTimeFormat("ru-RU", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  }).format(new Date(value));
}

export function AdminUsersPage() {
  const { auth } = useAuth();
  const queryClient = useQueryClient();
  const [roleDrafts, setRoleDrafts] = useState<RoleDrafts>({});
  const usersQuery = useQuery({ queryKey: ["admin", "users"], queryFn: getAdminUsers });

  const roleMutation = useMutation({
    mutationFn: ({ userId, role }: { userId: number; role: UserRole }) => (
      updateAdminUserRole(userId, { role })
    ),
    onSuccess: (updatedUser) => {
      queryClient.setQueryData<UserResponse[]>(["admin", "users"], (users) => (
        users?.map((user) => user.id === updatedUser.id ? updatedUser : user)
      ));
      setRoleDrafts((drafts) => {
        const nextDrafts = { ...drafts };
        delete nextDrafts[updatedUser.id];
        return nextDrafts;
      });
    },
  });

  const errorMessage = useApiErrorMessage(usersQuery.error || roleMutation.error);

  return (
    <section className="page-enter">
      <PageHeader
        description="Просмотр аккаунтов и назначение прав доступа."
        eyebrow="Admin"
        title="Пользователи"
      />

      {errorMessage ? <Alert type="error">{errorMessage}</Alert> : null}
      {roleMutation.isSuccess ? <Alert type="success">Роль пользователя обновлена.</Alert> : null}
      {usersQuery.isLoading ? <LoadingState /> : null}
      {usersQuery.data?.length === 0 ? <EmptyState title="Пользователи не найдены" /> : null}

      <div className="admin-list">
        {usersQuery.data?.map((user) => {
          const selectedRole = roleDrafts[user.id] ?? user.role;
          const isCurrentUser = user.id === auth?.id;
          const isUpdating = roleMutation.isPending && roleMutation.variables?.userId === user.id;

          return (
            <article className="admin-row user-admin-row" key={user.id}>
              <div className="user-admin-identity">
                <span className="user-avatar"><UserRoundCheck aria-hidden="true" size={19} /></span>
                <div>
                  <span>#{user.id}</span>
                  <strong>{user.username}</strong>
                  <p>{user.email}</p>
                </div>
                <span className={`status-badge ${user.enabled ? "status-active" : "status-disabled"}`}>
                  {user.enabled ? "Активен" : "Отключён"}
                </span>
              </div>

              <div className="user-admin-meta">
                <span>Создан: {formatCreatedAt(user.createdAt)}</span>
                {isCurrentUser ? <span>Текущий аккаунт</span> : null}
              </div>

              <div className="role-editor">
                <label>
                  Роль
                  <select
                    disabled={isCurrentUser || isUpdating}
                    onChange={(event) => setRoleDrafts((drafts) => ({
                      ...drafts,
                      [user.id]: event.target.value as UserRole,
                    }))}
                    value={selectedRole}
                  >
                    <option value="USER">USER</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                </label>
                <button
                  className="primary-button"
                  disabled={isCurrentUser || isUpdating || selectedRole === user.role}
                  onClick={() => roleMutation.mutate({ userId: user.id, role: selectedRole })}
                  type="button"
                >
                  <Save aria-hidden="true" size={17} />
                  {isUpdating ? "Сохранение..." : "Сохранить роль"}
                </button>
              </div>
            </article>
          );
        })}
      </div>

      <div className="admin-access-note">
        <ShieldCheck aria-hidden="true" size={18} />
        <span>Изменение ролей доступно только пользователям с ролью ADMIN.</span>
      </div>
    </section>
  );
}
