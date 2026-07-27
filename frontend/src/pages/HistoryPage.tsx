import { useQuery } from "@tanstack/react-query";
import { getMyHistory } from "../api/meApi";
import { Alert } from "../components/Alert";
import { EmptyState } from "../components/EmptyState";
import { LoadingState } from "../components/LoadingState";
import { PageHeader } from "../components/PageHeader";
import { useApiErrorMessage } from "../hooks/useApiErrorMessage";

export function HistoryPage() {
  const historyQuery = useQuery({ queryKey: ["me", "history"], queryFn: getMyHistory });
  const errorMessage = useApiErrorMessage(historyQuery.error);

  return (
    <section>
      <PageHeader
        eyebrow="History"
        title="История ответов"
        description="История текущего пользователя берется через /api/me/interview-history."
        actions={
          <button className="secondary-button" onClick={() => void historyQuery.refetch()} type="button">
            Обновить
          </button>
        }
      />

      {historyQuery.isLoading ? <LoadingState /> : null}
      {errorMessage ? <Alert type="error">{errorMessage}</Alert> : null}

      {historyQuery.data?.length === 0 ? (
        <EmptyState title="История пустая" description="Пройди интервью и отправь ответ, чтобы запись появилась здесь." />
      ) : null}

      <div className="history-list">
        {historyQuery.data?.map((item) => (
          <article className="history-item" key={item.questionId}>
            <div className="card-heading">
              <strong>{item.topicName ?? "Без темы"}</strong>
              <span>#{item.questionId}</span>
            </div>
            <p>{item.textQuestion}</p>
            <blockquote>{item.answerText ?? "Ответ не сохранен"}</blockquote>
            <small>{item.modelName ?? "model unknown"}</small>
          </article>
        ))}
      </div>
    </section>
  );
}
