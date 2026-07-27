import { Check, Settings2, X } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import type { BackgroundMode } from "../features/appearance/appearanceContext";
import { useAppearance } from "../features/appearance/useAppearance";

const BACKGROUNDS: Array<{
  id: BackgroundMode;
  name: string;
  description: string;
}> = [
  {
    id: "cosmos",
    name: "Космос",
    description: "Звёздный фон с мягким затемнением",
  },
  {
    id: "graphite",
    name: "Графит",
    description: "Спокойная нейтральная поверхность",
  },
  {
    id: "midnight",
    name: "Полночь",
    description: "Глубокий холодный фон без изображения",
  },
];

export function AppearanceSettings() {
  const { background, intensity, setBackground, setIntensity } = useAppearance();
  const [isOpen, setIsOpen] = useState(false);
  const closeButtonRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    if (!isOpen) {
      return;
    }

    closeButtonRef.current?.focus();
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        setIsOpen(false);
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [isOpen]);

  return (
    <>
      <button
        aria-label="Настройки оформления"
        className="icon-button"
        onClick={() => setIsOpen(true)}
        title="Настройки оформления"
        type="button"
      >
        <Settings2 aria-hidden="true" size={19} />
      </button>

      {isOpen ? (
        <div
          className="modal-backdrop"
          onMouseDown={(event) => {
            if (event.currentTarget === event.target) {
              setIsOpen(false);
            }
          }}
          role="presentation"
        >
          <section
            aria-labelledby="appearance-title"
            aria-modal="true"
            className="settings-modal"
            role="dialog"
          >
            <div className="modal-header">
              <div>
                <span className="eyebrow">Оформление</span>
                <h2 id="appearance-title">Фон приложения</h2>
              </div>
              <button
                aria-label="Закрыть настройки"
                className="icon-button"
                onClick={() => setIsOpen(false)}
                ref={closeButtonRef}
                title="Закрыть"
                type="button"
              >
                <X aria-hidden="true" size={20} />
              </button>
            </div>

            <div className="background-options" role="radiogroup" aria-label="Вариант фона">
              {BACKGROUNDS.map((option) => (
                <button
                  aria-checked={background === option.id}
                  className={`background-option background-option-${option.id}`}
                  key={option.id}
                  onClick={() => setBackground(option.id)}
                  role="radio"
                  type="button"
                >
                  <span className="background-preview" />
                  <span>
                    <strong>{option.name}</strong>
                    <small>{option.description}</small>
                  </span>
                  {background === option.id ? <Check aria-hidden="true" size={18} /> : null}
                </button>
              ))}
            </div>

            <label className="range-field">
              <span>
                Интенсивность
                <strong>{intensity}%</strong>
              </span>
              <input
                max="75"
                min="10"
                onChange={(event) => setIntensity(Number(event.target.value))}
                type="range"
                value={intensity}
              />
            </label>
          </section>
        </div>
      ) : null}
    </>
  );
}
