import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { CalendarDays, Mail, Save, ShieldCheck, UserRound } from "lucide-react";
import { useForm } from "react-hook-form";
import { getMe, updateMe } from "../api/meApi";
import { Alert } from "../components/Alert";
import { LoadingState } from "../components/LoadingState";
import { PageHeader } from "../components/PageHeader";
import {
  profileSchema,
  type ProfileFormValues,
} from "../features/profile/profileSchemas";
import { useAuth } from "../features/auth/useAuth";
import { useApiErrorMessage } from "../hooks/useApiErrorMessage";

function formatCreatedAt(value?: string) {
  if (!value) {
    return "Не указана";
  }

  return new Intl.DateTimeFormat("ru-RU", {
    day: "2-digit",
    month: "long",
    year: "numeric",
  }).format(new Date(value));
}

export function ProfilePage() {
  const queryClient = useQueryClient();
  const { auth, signIn } = useAuth();
  const meQuery = useQuery({ queryKey: ["me"], queryFn: getMe });
  const form = useForm<ProfileFormValues>({
    resolver: zodResolver(profileSchema),
    values: {
      username: meQuery.data?.username ?? auth?.username ?? "",
    },
  });

  const updateMutation = useMutation({
    mutationFn: updateMe,
    onSuccess: (user) => {
      queryClient.setQueryData(["me"], user);
      form.reset({ username: user.username });

      if (auth) {
        signIn({ ...auth, username: user.username });
      }
    },
  });

  const errorMessage = useApiErrorMessage(meQuery.error || updateMutation.error);
  const user = meQuery.data;

  function handleSubmit(values: ProfileFormValues) {
    updateMutation.mutate(values);
  }

  return (
    <section className="page-enter">
      <PageHeader
        description="Основные данные аккаунта и отображаемое имя."
        eyebrow="Аккаунт"
        title="Профиль"
      />

      {meQuery.isLoading ? <LoadingState /> : null}
      {errorMessage ? <Alert type="error">{errorMessage}</Alert> : null}
      {updateMutation.isSuccess ? <Alert type="success">Имя пользователя обновлено.</Alert> : null}

      <div className="profile-layout">
        <section className="panel profile-summary">
          <div className="profile-identity">
            <span className="profile-avatar">
              {(user?.username ?? auth?.username ?? "AI").slice(0, 2).toUpperCase()}
            </span>
            <div>
              <span className="eyebrow">Пользователь</span>
              <h2>{user?.username ?? auth?.username ?? "—"}</h2>
              <p>{user?.enabled === false ? "Аккаунт отключён" : "Аккаунт активен"}</p>
            </div>
          </div>

          <dl className="profile-details">
            <div>
              <dt><Mail aria-hidden="true" size={17} /> Email</dt>
              <dd>{user?.email ?? auth?.email ?? "—"}</dd>
            </div>
            <div>
              <dt><ShieldCheck aria-hidden="true" size={17} /> Роль</dt>
              <dd>{user?.role ?? auth?.role ?? "—"}</dd>
            </div>
            <div>
              <dt><CalendarDays aria-hidden="true" size={17} /> Дата регистрации</dt>
              <dd>{formatCreatedAt(user?.createdAt)}</dd>
            </div>
          </dl>
        </section>

        <section className="panel profile-settings">
          <div className="panel-heading">
            <div>
              <span className="eyebrow">Настройки</span>
              <h2>Отображаемое имя</h2>
            </div>
            <UserRound aria-hidden="true" size={20} />
          </div>

          <form className="stack-form" onSubmit={form.handleSubmit(handleSubmit)}>
            <label>
              Имя пользователя
              <input
                autoComplete="username"
                disabled={meQuery.isLoading || updateMutation.isPending}
                {...form.register("username")}
              />
              {form.formState.errors.username ? (
                <span className="field-error">{form.formState.errors.username.message}</span>
              ) : null}
            </label>

            <p className="form-hint">
              От 2 до 50 символов, без пробелов. Это имя отображается в интерфейсе.
            </p>

            <button
              className="primary-button compact"
              disabled={
                meQuery.isLoading ||
                updateMutation.isPending ||
                !form.formState.isDirty
              }
              type="submit"
            >
              <Save aria-hidden="true" size={17} />
              {updateMutation.isPending ? "Сохранение..." : "Сохранить"}
            </button>
          </form>
        </section>
      </div>
    </section>
  );
}
