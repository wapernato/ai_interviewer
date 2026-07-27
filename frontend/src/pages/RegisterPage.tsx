import { Link } from "react-router-dom";
import { AuthLayout } from "../components/AuthLayout";
import { RegisterForm } from "../features/auth/RegisterForm";

export function RegisterPage() {
  return (
    <AuthLayout
      description="Создай аккаунт, чтобы сохранять ответы и возвращаться к истории."
      eyebrow="Новый аккаунт"
      title="Регистрация"
    >
      <RegisterForm />
      <p className="auth-switch">
        Уже есть аккаунт? <Link to="/login">Войти</Link>
      </p>
    </AuthLayout>
  );
}
