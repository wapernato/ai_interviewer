type LoadingStateProps = {
  label?: string;
};

export function LoadingState({ label = "Загрузка" }: LoadingStateProps) {
  return <div className="loading-state">{label}</div>;
}
