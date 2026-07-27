import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  activateAiProfile,
  createAiProfile,
  deleteAiProfile,
  getAiProfiles,
  updateAiProfile,
} from "../api/aiProfilesApi";
import { Alert } from "../components/Alert";
import { ConfirmButton } from "../components/ConfirmButton";
import { EmptyState } from "../components/EmptyState";
import { LoadingState } from "../components/LoadingState";
import { PageHeader } from "../components/PageHeader";
import { AiProfileForm } from "../features/aiProfiles/AiProfileForm";
import type { AiProfileFormValues } from "../features/aiProfiles/aiProfileSchemas";
import { useApiErrorMessage } from "../hooks/useApiErrorMessage";

export function AdminAiProfilesPage() {
  const queryClient = useQueryClient();
  const profilesQuery = useQuery({ queryKey: ["ai-profiles"], queryFn: getAiProfiles });

  const invalidateProfiles = async () => {
    await queryClient.invalidateQueries({ queryKey: ["ai-profiles"] });
  };

  const createMutation = useMutation({ mutationFn: createAiProfile, onSuccess: invalidateProfiles });
  const updateMutation = useMutation({
    mutationFn: ({ id, values }: { id: number; values: AiProfileFormValues }) => updateAiProfile(id, values),
    onSuccess: invalidateProfiles,
  });
  const deleteMutation = useMutation({ mutationFn: deleteAiProfile, onSuccess: invalidateProfiles });
  const activateMutation = useMutation({ mutationFn: activateAiProfile, onSuccess: invalidateProfiles });

  const errorMessage = useApiErrorMessage(
    profilesQuery.error || createMutation.error || updateMutation.error || deleteMutation.error || activateMutation.error,
  );

  return (
    <section>
      <PageHeader
        eyebrow="Admin"
        title="Управление AI-профилями"
        description="Создание, обновление, удаление и активация профилей доступны только ADMIN."
      />

      {errorMessage ? <Alert type="error">{errorMessage}</Alert> : null}
      {createMutation.isSuccess || updateMutation.isSuccess || deleteMutation.isSuccess || activateMutation.isSuccess ? (
        <Alert type="success">Изменения сохранены.</Alert>
      ) : null}

      <section className="panel">
        <h2>Создать AI-профиль</h2>
        <AiProfileForm
          disabled={createMutation.isPending}
          submitLabel="Создать"
          onSubmit={(values) => createMutation.mutate(values)}
        />
      </section>

      {profilesQuery.isLoading ? <LoadingState /> : null}
      {profilesQuery.data?.length === 0 ? <EmptyState title="AI-профилей пока нет" /> : null}

      <div className="admin-list">
        {profilesQuery.data?.map((profile) => (
          <article className="admin-row profile-admin-row" key={profile.id}>
            <div className="card-heading">
              <div>
                <span>#{profile.id}</span>
                <strong>{profile.mode}</strong>
              </div>
              {profile.active ? <em>active</em> : null}
            </div>
            <AiProfileForm
              disabled={updateMutation.isPending}
              initialProfile={profile}
              submitLabel="Обновить"
              onSubmit={(values) => updateMutation.mutate({ id: profile.id, values })}
            />
            <div className="action-row">
              <button
                className="secondary-button"
                disabled={activateMutation.isPending || profile.active}
                onClick={() => activateMutation.mutate(profile.id)}
                type="button"
              >
                Активировать
              </button>
              <ConfirmButton
                className="danger-button"
                disabled={deleteMutation.isPending}
                message={`Удалить AI-профиль "${profile.mode}"?`}
                onConfirm={() => deleteMutation.mutate(profile.id)}
              >
                Удалить
              </ConfirmButton>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
