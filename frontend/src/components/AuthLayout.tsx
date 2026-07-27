import { Braces, MessagesSquare, Sparkles } from "lucide-react";
import type { ReactNode } from "react";

type AuthLayoutProps = {
  eyebrow: string;
  title: string;
  description: string;
  children: ReactNode;
};

export function AuthLayout({ eyebrow, title, description, children }: AuthLayoutProps) {
  return (
    <main className="auth-page">
      <section className="auth-visual">
        <div className="auth-brand">
          <span className="brand-mark"><Braces aria-hidden="true" size={22} /></span>
          <strong>AI Interviewer</strong>
        </div>
        <div className="auth-visual-copy">
          <span className="eyebrow"><Sparkles aria-hidden="true" size={14} /> Java Backend</span>
          <h1>Практика собеседований в рабочем темпе.</h1>
          <p>Вопрос, ответ и разбор собраны в одном спокойном пространстве.</p>
        </div>
        <div className="auth-visual-note">
          <MessagesSquare aria-hidden="true" size={20} />
          <span>Сфокусируйся на ответе. Остальное останется в истории.</span>
        </div>
      </section>

      <section className="auth-panel">
        <div className="auth-card">
          <span className="eyebrow">{eyebrow}</span>
          <h2>{title}</h2>
          <p>{description}</p>
          {children}
        </div>
        <footer className="auth-footer">AI Interviewer · Java Backend practice</footer>
      </section>
    </main>
  );
}
