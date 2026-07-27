import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { aiProfileSchema, type AiProfileFormValues } from "./aiProfileSchemas";
import type { AiProfileResponse } from "../../types/api";

type AiProfileFormProps = {
  initialProfile?: AiProfileResponse;
  disabled?: boolean;
  submitLabel: string;
  onSubmit: (values: AiProfileFormValues) => void;
};

const defaultValues: AiProfileFormValues = {
  mode: "",
  descriptionMode: "",
  instructionMode: "Задавай вопросы по Java Backend.",
  modelName: "mock-ai",
  language: "ru",
  answerStyle: "detailed",
  difficulty: "medium",
  feedbackMode: "detailed",
  hintMode: false,
  active: false,
  temperature: 0.7,
  maxTokens: 1000,
};

export function AiProfileForm({ initialProfile, disabled, submitLabel, onSubmit }: AiProfileFormProps) {
  const form = useForm<AiProfileFormValues>({
    resolver: zodResolver(aiProfileSchema),
    values: initialProfile
      ? {
          mode: initialProfile.mode,
          descriptionMode: initialProfile.descriptionMode ?? "",
          instructionMode: initialProfile.instructionMode,
          modelName: initialProfile.modelName,
          language: initialProfile.language,
          answerStyle: initialProfile.answerStyle ?? "",
          difficulty: initialProfile.difficulty,
          feedbackMode: initialProfile.feedbackMode,
          hintMode: initialProfile.hintMode,
          active: initialProfile.active,
          temperature: initialProfile.temperature,
          maxTokens: initialProfile.maxTokens,
        }
      : defaultValues,
  });

  return (
    <form className="stack-form" onSubmit={form.handleSubmit(onSubmit)}>
      <div className="form-grid">
        <label>
          Mode
          <input disabled={disabled} {...form.register("mode")} />
          {form.formState.errors.mode ? <span className="field-error">{form.formState.errors.mode.message}</span> : null}
        </label>
        <label>
          Model
          <input disabled={disabled} {...form.register("modelName")} />
          {form.formState.errors.modelName ? (
            <span className="field-error">{form.formState.errors.modelName.message}</span>
          ) : null}
        </label>
        <label>
          Language
          <select disabled={disabled} {...form.register("language")}>
            <option value="ru">ru</option>
            <option value="en">en</option>
          </select>
        </label>
        <label>
          Difficulty
          <select disabled={disabled} {...form.register("difficulty")}>
            <option value="easy">easy</option>
            <option value="medium">medium</option>
            <option value="hard">hard</option>
          </select>
        </label>
        <label>
          Feedback
          <select disabled={disabled} {...form.register("feedbackMode")}>
            <option value="short">short</option>
            <option value="detailed">detailed</option>
            <option value="strict">strict</option>
          </select>
        </label>
        <label>
          Temperature
          <input disabled={disabled} step="0.1" type="number" {...form.register("temperature", { valueAsNumber: true })} />
        </label>
        <label>
          Max tokens
          <input disabled={disabled} type="number" {...form.register("maxTokens", { valueAsNumber: true })} />
        </label>
        <label>
          Answer style
          <input disabled={disabled} {...form.register("answerStyle")} />
        </label>
      </div>

      <label>
        Description
        <textarea disabled={disabled} rows={3} {...form.register("descriptionMode")} />
      </label>

      <label>
        Instruction
        <textarea disabled={disabled} rows={5} {...form.register("instructionMode")} />
        {form.formState.errors.instructionMode ? (
          <span className="field-error">{form.formState.errors.instructionMode.message}</span>
        ) : null}
      </label>

      <div className="checkbox-row">
        <label>
          <input disabled={disabled} type="checkbox" {...form.register("hintMode")} />
          Hint mode
        </label>
        <label>
          <input disabled={disabled} type="checkbox" {...form.register("active")} />
          Active
        </label>
      </div>

      <button className="primary-button compact" disabled={disabled} type="submit">
        {submitLabel}
      </button>
    </form>
  );
}
