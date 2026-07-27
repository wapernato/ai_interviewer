import { z } from "zod";

export const profileSchema = z.object({
  username: z
    .string()
    .trim()
    .min(2, "Минимум 2 символа.")
    .max(50, "Максимум 50 символов.")
    .regex(/^\S+$/, "Имя не должно содержать пробелы."),
});

export type ProfileFormValues = z.infer<typeof profileSchema>;
