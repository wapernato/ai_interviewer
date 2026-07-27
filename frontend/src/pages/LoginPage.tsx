import { Link } from "react-router-dom";
import { AuthLayout } from "../components/AuthLayout";
import { LoginForm } from "../features/auth/LoginForm";

export function LoginPage() {
  return (
    <AuthLayout
      description="Продолжи тренировку или начни новое интервью."
      eyebrow="С возвращением"
      title="Вход"
    >
      <LoginForm />
      <p className="auth-switch">
        Нет аккаунта? <Link to="/register">Зарегистрироваться</Link>
      </p>
    </AuthLayout>
  );
}
