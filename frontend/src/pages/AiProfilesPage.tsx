import { useQuery } from "@tanstack/react-query";
import { getAiProfiles } from "../api/aiProfilesApi";
import { Alert } from "../components/Alert";
import { EmptyState } from "../components/EmptyState";
import { LoadingState } from "../components/LoadingState";
import { PageHeader } from "../components/PageHeader";
import { useApiErrorMessage } from "../hooks/useApiErrorMessage";

export function AiProfilesPage() {
  const profilesQuery = useQuery({ queryKey: ["ai-profiles"], queryFn: getAiProfiles });
  const errorMessage = useApiErrorMessage(profilesQuery.error);

  return (
    <section>
      <PageHeader
        eyebrow="AI Profiles"
        title="AI-профили"
        description="Публичное чтение профилей. Создание, удаление и активация доступны только ADMIN."
      />

      {profilesQuery.isLoading ? <LoadingState /> : null}
      {errorMessage ? <Alert type="error">{errorMessage}</Alert> : null}

      {profilesQuery.data?.length === 0 ? (
        <EmptyState title="AI-профили не найдены" description="Проверь Flyway seed или admin настройки." />
      ) : null}

      <div className="card-grid">
        {profilesQuery.data?.map((profile) => (
          <article className="data-card profile-card" key={profile.id}>
            <div className="card-heading">
              <span>#{profile.id}</span>
              {profile.active ? <em>active</em> : null}
            </div>
            <strong>{profile.mode}</strong>
            <p>{profile.descriptionMode || "Описание не указано."}</p>
            <dl>
              <div><dt>Model</dt><dd>{profile.modelName}</dd></div>
              <div><dt>Language</dt><dd>{profile.language}</dd></div>
              <div><dt>Difficulty</dt><dd>{profile.difficulty}</dd></div>
              <div><dt>Feedback</dt><dd>{profile.feedbackMode}</dd></div>
            </dl>
          </article>
        ))}
      </div>
    </section>
  );
}
