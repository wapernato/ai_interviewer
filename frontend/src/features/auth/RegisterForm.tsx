import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation } from "@tanstack/react-query";
import { useForm, useWatch } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../../api/authApi";
import { Alert } from "../../components/Alert";
import { useAuth } from "./useAuth";
import { normalizeApiError } from "../../utils/apiError";
import { registerSchema, type RegisterFormValues } from "./authSchemas";
import { PasswordStrengthFeedback } from "./PasswordStrengthFeedback";

export function RegisterForm() {
  const navigate = useNavigate();
  const { signIn } = useAuth();

  const form = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      username: "",
      email: "",
      password: "",
    },
  });

  const mutation = useMutation({
    mutationFn: registerUser,
    onSuccess: (auth) => {
      signIn(auth);
      navigate("/", { replace: true });
    },
  });

  function handleSubmit(values: RegisterFormValues) {
    mutation.mutate(values);
  }

  const apiError = mutation.error ? normalizeApiError(mutation.error) : null;
  const password = useWatch({ control: form.control, name: "password" });

  return (
    <form className="auth-form" onSubmit={form.handleSubmit(handleSubmit)}>
      {apiError ? <Alert type="error">{apiError.message}</Alert> : null}

      <label>
        Имя пользователя
        <input autoComplete="username" {...form.register("username")} />
        {form.formState.errors.username ? (
          <span className="field-error">{form.formState.errors.username.message}</span>
        ) : null}
      </label>

      <label>
        Email
        <input type="email" autoComplete="email" {...form.register("email")} />
        {form.formState.errors.email ? <span className="field-error">{form.formState.errors.email.message}</span> : null}
      </label>

      <label>
        Пароль
        <input type="password" autoComplete="new-password" {...form.register("password")} />
        {form.formState.errors.password ? (
          <span className="field-error">{form.formState.errors.password.message}</span>
        ) : null}
      </label>

      <PasswordStrengthFeedback password={password} />

      <button className="primary-button" disabled={mutation.isPending} type="submit">
        {mutation.isPending ? "Регистрация..." : "Зарегистрироваться"}
      </button>
    </form>
  );
}
