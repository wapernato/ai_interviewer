import { z } from "zod";

export const aiProfileSchema = z.object({
  mode: z.string().trim().min(2, "Минимум 2 символа.").max(100),
  descriptionMode: z.string().max(1000).optional(),
  instructionMode: z.string().trim().min(10, "Минимум 10 символов.").max(4000),
  modelName: z.string().trim().min(1, "Модель обязательна.").max(100),
  language: z.enum(["ru", "en"]),
  answerStyle: z.string().max(100).optional(),
  difficulty: z.enum(["easy", "medium", "hard"]),
  feedbackMode: z.enum(["short", "detailed", "strict"]),
  hintMode: z.boolean(),
  active: z.boolean(),
  temperature: z.number().min(0).max(2),
  maxTokens: z.number().int().min(1).max(4000),
});

export type AiProfileFormValues = z.infer<typeof aiProfileSchema>;
