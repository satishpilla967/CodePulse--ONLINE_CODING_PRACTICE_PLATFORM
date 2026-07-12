import z from "zod";

export const loginFormSchema = z.object({
  email: z.string().trim().min(1).max(320).email(),
  password: z.string().min(1).max(72),
});

export const registerFormSchema = z.object({
  email: z.string().trim().min(1).max(320).email(),
  password: z.string().min(8).max(72),
  nickname: z.string().trim().max(255).optional(),
  becomeAdmin: z.boolean().optional(),
});

export const passwordResetRequestFormSchema = z.object({
  email: z.string().trim().min(1).max(320).email(),
});

export const passwordResetConfirmFormSchema = z
  .object({
    newPassword: z.string().min(8).max(72),
    confirmPassword: z.string().min(8).max(72),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"],
  });
