import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { useLocation, useNavigate } from "react-router-dom";
import { loginUser } from "../../api/authApi";
import { useAuth } from "./useAuth";
import { Alert } from "../../components/Alert";
import { normalizeApiError } from "../../utils/apiError";
import { loginSchema, type LoginFormValues } from "./authSchemas";

type LocationState = {
  from?: {
    pathname?: string;
  };
};

export function LoginForm() {
  const navigate = useNavigate();
  const location = useLocation();
  const { signIn } = useAuth();
  const from = (location.state as LocationState | null)?.from?.pathname ?? "/";

  const form = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const mutation = useMutation({
    mutationFn: loginUser,
    onSuccess: (auth) => {
      signIn(auth);
      navigate(from, { replace: true });
    },
  });

  function handleSubmit(values: LoginFormValues) {
    mutation.mutate(values);
  }

  const apiError = mutation.error ? normalizeApiError(mutation.error) : null;

  return (
    <form className="auth-form" onSubmit={form.handleSubmit(handleSubmit)}>
      {apiError ? <Alert type="error">{apiError.message}</Alert> : null}

      <label>
        Email
        <input type="email" autoComplete="email" {...form.register("email")} />
        {form.formState.errors.email ? <span className="field-error">{form.formState.errors.email.message}</span> : null}
      </label>

      <label>
        Пароль
        <input type="password" autoComplete="current-password" {...form.register("password")} />
        {form.formState.errors.password ? (
          <span className="field-error">{form.formState.errors.password.message}</span>
        ) : null}
      </label>

      <button className="primary-button" disabled={mutation.isPending} type="submit">
        {mutation.isPending ? "Вход..." : "Войти"}
      </button>
    </form>
  );
}
