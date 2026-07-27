import type { ReactNode } from "react";

type AlertProps = {
  type: "error" | "success" | "info";
  title?: string;
  children: ReactNode;
};

export function Alert({ type, title, children }: AlertProps) {
  return (
    <div className={`alert alert-${type}`} role={type === "error" ? "alert" : "status"}>
      {title ? <strong>{title}</strong> : null}
      <span>{children}</span>
    </div>
  );
}
