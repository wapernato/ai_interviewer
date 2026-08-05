import {
  Bot,
  Braces,
  History,
  LayoutDashboard,
  LibraryBig,
  LogOut,
  Menu,
  MessagesSquare,
  ShieldCheck,
  UserRound,
  UsersRound,
  X,
} from "lucide-react";
import { useState } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { AppearanceSettings } from "../components/AppearanceSettings";
import { AppFooter } from "../components/AppFooter";
import { useAuth } from "../features/auth/useAuth";

export function AppLayout() {
  const { auth, role, signOut } = useAuth();
  const navigate = useNavigate();
  const [isNavigationOpen, setIsNavigationOpen] = useState(false);

  function handleSignOut() {
    signOut();
    navigate("/login");
  }

  const closeNavigation = () => setIsNavigationOpen(false);

  return (
    <div className={`app-shell ${isNavigationOpen ? "navigation-open" : ""}`}>
      <button
        aria-label="Закрыть навигацию"
        className="navigation-backdrop"
        onClick={closeNavigation}
        type="button"
      />

      <aside className="sidebar" aria-label="Боковая панель">
        <div className="sidebar-header">
          <div className="brand">
            <span className="brand-mark"><Braces aria-hidden="true" size={22} /></span>
            <div>
              <strong>AI Interviewer</strong>
              <span>Java Backend</span>
            </div>
          </div>
          <button
            aria-label="Закрыть меню"
            className="icon-button sidebar-close"
            onClick={closeNavigation}
            title="Закрыть меню"
            type="button"
          >
            <X aria-hidden="true" size={20} />
          </button>
        </div>

        <nav className="nav-list" aria-label="Основная навигация">
          <NavLink onClick={closeNavigation} to="/" end>
            <LayoutDashboard aria-hidden="true" size={19} />
            <span>Главная</span>
          </NavLink>
          <NavLink onClick={closeNavigation} to="/topics">
            <LibraryBig aria-hidden="true" size={19} />
            <span>Темы</span>
          </NavLink>
          <NavLink onClick={closeNavigation} to="/interview">
            <MessagesSquare aria-hidden="true" size={19} />
            <span>Интервью</span>
          </NavLink>
          <NavLink onClick={closeNavigation} to="/ai-profiles">
            <Bot aria-hidden="true" size={19} />
            <span>AI-профили</span>
          </NavLink>
          <NavLink onClick={closeNavigation} to="/history">
            <History aria-hidden="true" size={19} />
            <span>История</span>
          </NavLink>
          <NavLink onClick={closeNavigation} to="/profile">
            <UserRound aria-hidden="true" size={19} />
            <span>Профиль</span>
          </NavLink>
        </nav>

        {role === "ADMIN" ? (
          <nav className="nav-list admin-nav" aria-label="Администрирование">
            <span>Администрирование</span>
            <NavLink onClick={closeNavigation} to="/admin/topics">
              <ShieldCheck aria-hidden="true" size={19} />
              <span>Управление темами</span>
            </NavLink>
            <NavLink onClick={closeNavigation} to="/admin/ai-profiles">
              <ShieldCheck aria-hidden="true" size={19} />
              <span>Управление AI</span>
            </NavLink>
            <NavLink onClick={closeNavigation} to="/admin/users">
              <UsersRound aria-hidden="true" size={19} />
              <span>Пользователи</span>
            </NavLink>
          </nav>
        ) : null}

        <div className="sidebar-account">
          <NavLink className="sidebar-profile-link" onClick={closeNavigation} to="/profile">
            <span className="user-avatar">{auth?.username?.slice(0, 2).toUpperCase() ?? "AI"}</span>
            <span>
              <strong>{auth?.username ?? "Гость"}</strong>
              <small>{auth?.role ?? "USER"}</small>
            </span>
          </NavLink>
          <button
            aria-label="Выйти"
            className="icon-button"
            onClick={handleSignOut}
            title="Выйти"
            type="button"
          >
            <LogOut aria-hidden="true" size={18} />
          </button>
        </div>
      </aside>

      <div className="main-area">
        <header className="topbar">
          <div className="topbar-leading">
            <button
              aria-label="Открыть меню"
              className="icon-button menu-button"
              onClick={() => setIsNavigationOpen(true)}
              title="Открыть меню"
              type="button"
            >
              <Menu aria-hidden="true" size={20} />
            </button>
            <div>
              <strong>Рабочая область</strong>
              <span>{auth?.email ?? "Не авторизован"}</span>
            </div>
          </div>
          <div className="topbar-actions">
            <AppearanceSettings />
            <span className="topbar-avatar">{auth?.username?.slice(0, 2).toUpperCase() ?? "AI"}</span>
          </div>
        </header>

        <main className="content">
          <Outlet />
        </main>
        <AppFooter />
      </div>
    </div>
  );
}
