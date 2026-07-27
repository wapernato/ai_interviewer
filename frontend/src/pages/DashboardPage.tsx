import { ArrowRight, Bot, LibraryBig, ShieldCheck, UserRound } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { getMe } from "../api/meApi";
import { getTopics } from "../api/topicsApi";
import { getAiProfiles } from "../api/aiProfilesApi";
import { Alert } from "../components/Alert";
import { LoadingState } from "../components/LoadingState";
import { useAuth } from "../features/auth/useAuth";
import { useApiErrorMessage } from "../hooks/useApiErrorMessage";
import cosmicBackground from "../assets/cosmic-background.jpg";

export function DashboardPage() {
  const { auth } = useAuth();
  const meQuery = useQuery({ queryKey: ["me"], queryFn: getMe });
  const topicsQuery = useQuery({ queryKey: ["topics"], queryFn: getTopics });
  const profilesQuery = useQuery({ queryKey: ["ai-profiles"], queryFn: getAiProfiles });
  const errorMessage = useApiErrorMessage(meQuery.error || topicsQuery.error || profilesQuery.error);
  const activeProfile = profilesQuery.data?.find((profile) => profile.active);

  return (
    <section className="page-enter">
      <section className="dashboard-hero">
        <div
          aria-hidden="true"
          className="dashboard-hero-media"
          style={{ backgroundImage: `url(${cosmicBackground})` }}
        />
        <div className="dashboard-hero-content">
          <span className="eyebrow">Java Backend practice</span>
          <h1>Готовы к следующему вопросу?</h1>
          <p>Выберите тему, сформулируйте ответ и получите разбор в одном рабочем потоке.</p>
          <Link className="primary-button compact" to="/interview">
            Начать интервью
            <ArrowRight aria-hidden="true" size={18} />
          </Link>
        </div>
      </section>

      {errorMessage ? <Alert type="error">{errorMessage}</Alert> : null}
      {meQuery.isLoading || topicsQuery.isLoading || profilesQuery.isLoading ? <LoadingState /> : null}

      <div className="metric-grid">
        <article className="metric-card">
          <div className="metric-card-header">
            <span>Пользователь</span>
            <UserRound aria-hidden="true" size={19} />
          </div>
          <strong>{meQuery.data?.username ?? auth?.username ?? "-"}</strong>
          <p>{meQuery.data?.email ?? auth?.email ?? "-"}</p>
        </article>
        <article className="metric-card">
          <div className="metric-card-header">
            <span>Роль</span>
            <ShieldCheck aria-hidden="true" size={19} />
          </div>
          <strong>{meQuery.data?.role ?? auth?.role ?? "-"}</strong>
          <p>Уровень доступа к рабочим разделам.</p>
        </article>
        <article className="metric-card">
          <div className="metric-card-header">
            <span>Темы</span>
            <LibraryBig aria-hidden="true" size={19} />
          </div>
          <strong>{topicsQuery.data?.length ?? 0}</strong>
          <p>Доступно для текущего интервью.</p>
        </article>
        <article className="metric-card">
          <div className="metric-card-header">
            <span>AI-профиль</span>
            <Bot aria-hidden="true" size={19} />
          </div>
          <strong>{activeProfile?.mode ?? "Не найден"}</strong>
          <p>{activeProfile?.modelName ?? "Активный профиль пока не выбран."}</p>
        </article>
      </div>
    </section>
  );
}
