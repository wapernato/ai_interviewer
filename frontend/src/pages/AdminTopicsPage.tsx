import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { createTopic, deleteTopic, getTopics, updateTopic } from "../api/topicsApi";
import { Alert } from "../components/Alert";
import { ConfirmButton } from "../components/ConfirmButton";
import { EmptyState } from "../components/EmptyState";
import { LoadingState } from "../components/LoadingState";
import { PageHeader } from "../components/PageHeader";
import { TopicForm } from "../features/topics/TopicForm";
import { useApiErrorMessage } from "../hooks/useApiErrorMessage";
import type { TopicFormValues } from "../features/topics/topicSchemas";

export function AdminTopicsPage() {
  const queryClient = useQueryClient();
  const topicsQuery = useQuery({ queryKey: ["topics"], queryFn: getTopics });

  const invalidateTopics = async () => {
    await queryClient.invalidateQueries({ queryKey: ["topics"] });
  };

  const createMutation = useMutation({ mutationFn: createTopic, onSuccess: invalidateTopics });
  const updateMutation = useMutation({
    mutationFn: ({ id, values }: { id: number; values: TopicFormValues }) => updateTopic(id, values),
    onSuccess: invalidateTopics,
  });
  const deleteMutation = useMutation({ mutationFn: deleteTopic, onSuccess: invalidateTopics });

  const errorMessage = useApiErrorMessage(
    topicsQuery.error || createMutation.error || updateMutation.error || deleteMutation.error,
  );

  return (
    <section>
      <PageHeader
        eyebrow="Admin"
        title="Управление темами"
        description="Создание, изменение и удаление тем выполняется через /api/admin/topics."
      />

      {errorMessage ? <Alert type="error">{errorMessage}</Alert> : null}
      {createMutation.isSuccess || updateMutation.isSuccess || deleteMutation.isSuccess ? (
        <Alert type="success">Изменения сохранены.</Alert>
      ) : null}

      <section className="panel">
        <h2>Создать тему</h2>
        <TopicForm disabled={createMutation.isPending} submitLabel="Создать" onSubmit={(values) => createMutation.mutate(values)} />
      </section>

      {topicsQuery.isLoading ? <LoadingState /> : null}
      {topicsQuery.data?.length === 0 ? <EmptyState title="Тем пока нет" /> : null}

      <div className="admin-list">
        {topicsQuery.data?.map((topic) => (
          <article className="admin-row" key={topic.id}>
            <div>
              <span>#{topic.id}</span>
              <strong>{topic.name}</strong>
            </div>
            <TopicForm
              disabled={updateMutation.isPending}
              initialValue={topic.name}
              submitLabel="Обновить"
              onSubmit={(values) => updateMutation.mutate({ id: topic.id, values })}
            />
            <ConfirmButton
              className="danger-button"
              disabled={deleteMutation.isPending}
              message={`Удалить тему "${topic.name}"?`}
              onConfirm={() => deleteMutation.mutate(topic.id)}
            >
              Удалить
            </ConfirmButton>
          </article>
        ))}
      </div>
    </section>
  );
}
