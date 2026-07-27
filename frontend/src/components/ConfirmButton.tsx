import type { ReactNode } from "react";

type ConfirmButtonProps = {
  children: ReactNode;
  message: string;
  disabled?: boolean;
  className?: string;
  onConfirm: () => void;
};

export function ConfirmButton({ children, message, disabled, className, onConfirm }: ConfirmButtonProps) {
  function handleClick() {
    if (window.confirm(message)) {
      onConfirm();
    }
  }

  return (
    <button className={className} disabled={disabled} onClick={handleClick} type="button">
      {children}
    </button>
  );
}
