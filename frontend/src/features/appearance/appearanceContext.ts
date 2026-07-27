import { createContext } from "react";

export type BackgroundMode = "cosmos" | "graphite" | "midnight";

export type AppearanceContextValue = {
  background: BackgroundMode;
  intensity: number;
  setBackground: (background: BackgroundMode) => void;
  setIntensity: (intensity: number) => void;
};

export const AppearanceContext = createContext<AppearanceContextValue | null>(null);
