import { z } from "zod";

export const topicSchema = z.object({
  name: z.string().trim().min(2, "Минимум 2 символа.").max(100, "Максимум 100 символов."),
});

export type TopicFormValues = z.infer<typeof topicSchema>;
