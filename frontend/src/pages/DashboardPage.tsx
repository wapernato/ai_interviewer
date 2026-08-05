import {
  ArrowRight,
  Bot,
  ChartNoAxesColumnIncreasing,
  CircleDashed,
  CircleHelp,
  LibraryBig,
  MessageSquareText,
  ShieldCheck,
  UserRound,
} from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { getMe, getMyStatistics } from "../api/meApi";
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
  const statisticsQuery = useQuery({ queryKey: ["me", "statistics"], queryFn: getMyStatistics });
  const topicsQuery = useQuery({ queryKey: ["topics"], queryFn: getTopics });
  const profilesQuery = useQuery({ queryKey: ["ai-profiles"], queryFn: getAiProfiles });
  const errorMessage = useApiErrorMessage(
    meQuery.error || statisticsQuery.error || topicsQuery.error || profilesQuery.error,
  );
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
      {meQuery.isLoading || statisticsQuery.isLoading || topicsQuery.isLoading || profilesQuery.isLoading ? (
        <LoadingState />
      ) : null}

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

      <div className="dashboard-section-header">
        <div>
          <span className="eyebrow">Прогресс</span>
          <h2>Статистика интервью</h2>
        </div>
      </div>

      <div className="metric-grid">
        <article className="metric-card">
          <div className="metric-card-header">
            <span>Вопросы</span>
            <CircleHelp aria-hidden="true" size={19} />
          </div>
          <strong>{statisticsQuery.data?.totalQuestions ?? 0}</strong>
          <p>Всего получено вопросов.</p>
        </article>
        <article className="metric-card">
          <div className="metric-card-header">
            <span>Ответы</span>
            <MessageSquareText aria-hidden="true" size={19} />
          </div>
          <strong>{statisticsQuery.data?.totalAnswers ?? 0}</strong>
          <p>Ответов отправлено пользователем.</p>
        </article>
        <article className="metric-card">
          <div className="metric-card-header">
            <span>Без ответа</span>
            <CircleDashed aria-hidden="true" size={19} />
          </div>
          <strong>{statisticsQuery.data?.unansweredQuestions ?? 0}</strong>
          <p>Вопросов ещё ожидают ответа.</p>
        </article>
        <article className="metric-card">
          <div className="metric-card-header">
            <span>Завершено</span>
            <ChartNoAxesColumnIncreasing aria-hidden="true" size={19} />
          </div>
          <strong>{Math.round(statisticsQuery.data?.completionRate ?? 0)}%</strong>
          <p>Доля вопросов с отправленным ответом.</p>
        </article>
      </div>
    </section>
  );
}
