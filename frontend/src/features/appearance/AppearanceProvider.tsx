import {
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import {
  AppearanceContext,
  type AppearanceContextValue,
  type BackgroundMode,
} from "./appearanceContext";

type AppearanceSettings = {
  background: BackgroundMode;
  intensity: number;
};

const STORAGE_KEY = "ai-interviewer-appearance";
const DEFAULT_SETTINGS: AppearanceSettings = {
  background: "cosmos",
  intensity: 42,
};

function readSettings(): AppearanceSettings {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (!stored) {
      return DEFAULT_SETTINGS;
    }

    const parsed = JSON.parse(stored) as Partial<AppearanceSettings>;
    const background = ["cosmos", "graphite", "midnight"].includes(parsed.background ?? "")
      ? (parsed.background as BackgroundMode)
      : DEFAULT_SETTINGS.background;
    const intensity =
      typeof parsed.intensity === "number"
        ? Math.min(75, Math.max(10, parsed.intensity))
        : DEFAULT_SETTINGS.intensity;

    return { background, intensity };
  } catch {
    return DEFAULT_SETTINGS;
  }
}

export function AppearanceProvider({ children }: { children: ReactNode }) {
  const [settings, setSettings] = useState<AppearanceSettings>(readSettings);

  useEffect(() => {
    document.documentElement.dataset.background = settings.background;
    document.documentElement.style.setProperty(
      "--background-art-opacity",
      String(settings.intensity / 100),
    );
    localStorage.setItem(STORAGE_KEY, JSON.stringify(settings));
  }, [settings]);

  const value = useMemo<AppearanceContextValue>(
    () => ({
      ...settings,
      setBackground: (background) => setSettings((current) => ({ ...current, background })),
      setIntensity: (intensity) => setSettings((current) => ({ ...current, intensity })),
    }),
    [settings],
  );

  return <AppearanceContext.Provider value={value}>{children}</AppearanceContext.Provider>;
}
