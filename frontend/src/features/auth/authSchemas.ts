import { z } from "zod";

export const loginSchema = z.object({
  email: z.string().trim().min(1, "Email обязателен.").email("Введите корректный email."),
  password: z.string().min(1, "Пароль обязателен."),
});

export const registerSchema = z.object({
  username: z.string().trim().min(2, "Минимум 2 символа.").max(50, "Максимум 50 символов."),
  email: z.string().trim().min(1, "Email обязателен.").email("Введите корректный email.").max(100),
  password: z
    .string()
    .min(8, "Минимум 8 символов.")
    .max(72, "Максимум 72 символа.")
    .regex(/[a-z]/, "Добавьте хотя бы одну строчную букву.")
    .regex(/[A-Z]/, "Добавьте хотя бы одну заглавную букву.")
    .regex(/[0-9]/, "Добавьте хотя бы одну цифру.")
    .regex(/[^a-zA-Z0-9\s]/, "Добавьте хотя бы один специальный символ.")
    .refine((password) => !/\s/.test(password), "Пароль не должен содержать пробелы."),
});

export type LoginFormValues = z.infer<typeof loginSchema>;
export type RegisterFormValues = z.infer<typeof registerSchema>;
