import { Link } from "react-router-dom";

export function ForbiddenPage() {
  return (
    <main className="simple-page">
      <h1>Нет доступа</h1>
      <p>Для этого раздела нужна роль ADMIN.</p>
      <Link className="primary-link" to="/">Вернуться на главную</Link>
    </main>
  );
}
