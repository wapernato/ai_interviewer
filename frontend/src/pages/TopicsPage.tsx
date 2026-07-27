import { useQuery } from "@tanstack/react-query";
import { getTopics } from "../api/topicsApi";
import { Alert } from "../components/Alert";
import { EmptyState } from "../components/EmptyState";
import { LoadingState } from "../components/LoadingState";
import { PageHeader } from "../components/PageHeader";
import { useApiErrorMessage } from "../hooks/useApiErrorMessage";

export function TopicsPage() {
  const topicsQuery = useQuery({ queryKey: ["topics"], queryFn: getTopics });
  const errorMessage = useApiErrorMessage(topicsQuery.error);

  return (
    <section>
      <PageHeader
        eyebrow="Topics"
        title="Темы собеседования"
        description="Read-only список тем из backend. Изменение тем доступно только администратору."
      />

      {topicsQuery.isLoading ? <LoadingState /> : null}
      {errorMessage ? <Alert type="error">{errorMessage}</Alert> : null}

      {topicsQuery.data?.length === 0 ? (
        <EmptyState title="Тем пока нет" description="Добавить темы может пользователь с ролью ADMIN." />
      ) : null}

      <div className="card-grid">
        {topicsQuery.data?.map((topic) => (
          <article className="data-card" key={topic.id}>
            <span>#{topic.id}</span>
            <strong>{topic.name}</strong>
          </article>
        ))}
      </div>
    </section>
  );
}
