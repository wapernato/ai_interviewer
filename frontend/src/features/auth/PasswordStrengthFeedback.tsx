import { useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { getPasswordStrength } from "../../api/authApi";
import type { PasswordStrengthLevel } from "../../types/api";

type PasswordStrengthFeedbackProps = {
  password: string;
};

const levelLabels: Record<PasswordStrengthLevel, string> = {
  WEAK: "Слабый",
  MEDIUM: "Средний",
  STRONG: "Сильный",
};

const levelValues: Record<PasswordStrengthLevel, number> = {
  WEAK: 1,
  MEDIUM: 2,
  STRONG: 3,
};

export function PasswordStrengthFeedback({ password }: PasswordStrengthFeedbackProps) {
  const [debouncedPassword, setDebouncedPassword] = useState(password);

  useEffect(() => {
    const timeoutId = window.setTimeout(
      () => setDebouncedPassword(password),
      password ? 300 : 0,
    );
    return () => window.clearTimeout(timeoutId);
  }, [password]);

  const strengthQuery = useQuery({
    queryKey: ["password-strength", debouncedPassword],
    queryFn: ({ signal }) => getPasswordStrength({ password: debouncedPassword }, signal),
    enabled: debouncedPassword.trim().length > 0,
    retry: false,
    staleTime: 60_000,
  });

  if (!password) {
    return null;
  }

  const result = strengthQuery.data;
  const activeSegments = result ? levelValues[result.level] : 0;

  return (
    <div className="password-strength" aria-live="polite">
      <div className="password-strength-heading">
        <span>Надёжность пароля</span>
        <strong className={result ? `strength-${result.level.toLowerCase()}` : undefined}>
          {strengthQuery.isFetching ? "Проверка..." : result ? levelLabels[result.level] : "—"}
        </strong>
      </div>
      <div className="password-strength-meter" aria-hidden="true">
        {[1, 2, 3].map((segment) => (
          <span
            className={segment <= activeSegments && result ? `strength-${result.level.toLowerCase()}` : undefined}
            key={segment}
          />
        ))}
      </div>
      {result?.suggestions.length ? (
        <ul className="password-suggestions">
          {result.suggestions.map((suggestion) => <li key={suggestion}>{suggestion}</li>)}
        </ul>
      ) : null}
      {strengthQuery.isError ? <span className="field-error">Не удалось оценить пароль.</span> : null}
    </div>
  );
}
