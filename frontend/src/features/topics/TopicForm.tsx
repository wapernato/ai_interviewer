import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { topicSchema, type TopicFormValues } from "./topicSchemas";

type TopicFormProps = {
  initialValue?: string;
  submitLabel: string;
  disabled?: boolean;
  onSubmit: (values: TopicFormValues) => void;
};

export function TopicForm({ initialValue = "", submitLabel, disabled, onSubmit }: TopicFormProps) {
  const form = useForm<TopicFormValues>({
    resolver: zodResolver(topicSchema),
    values: { name: initialValue },
  });

  return (
    <form className="inline-form" onSubmit={form.handleSubmit(onSubmit)}>
      <label>
        Название темы
        <input disabled={disabled} {...form.register("name")} />
        {form.formState.errors.name ? <span className="field-error">{form.formState.errors.name.message}</span> : null}
      </label>
      <button className="primary-button compact" disabled={disabled} type="submit">
        {submitLabel}
      </button>
    </form>
  );
}
