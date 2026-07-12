import { passwordResetConfirmFormSchema } from "@/lib/api/schema/auth";
import { usePasswordResetConfirmMutation } from "@/lib/api/queries/auth";
import { Button, PasswordInput, Stack } from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { zodResolver } from "mantine-form-zod-resolver";
import { useNavigate } from "react-router-dom";
import z from "zod";

export default function PasswordResetConfirmButton({
  token,
  className,
}: {
  token: string;
  className?: string;
}) {
  const { mutate, isPending } = usePasswordResetConfirmMutation();
  const navigate = useNavigate();

  const form = useForm({
    validate: zodResolver(passwordResetConfirmFormSchema),
    initialValues: {
      newPassword: "",
      confirmPassword: "",
    },
  });

  const onSubmit = (values: z.infer<typeof passwordResetConfirmFormSchema>) => {
    mutate(
      { token, newPassword: values.newPassword },
      {
        onSuccess: (data) => {
          if (data.success) {
            navigate("/login?success=true&message=Your password has been updated. Please log in.");
            return;
          }

          notifications.show({
            message: data.message,
            color: "red",
          });
        },
        onError: () => {
          notifications.show({
            message: "This reset link is invalid or has expired. Please request a new one.",
            color: "red",
          });
        },
      },
    );
  };

  return (
    <form onSubmit={form.onSubmit(onSubmit)} className={className}>
      <Stack gap="sm">
        <PasswordInput
          label="New password"
          placeholder="At least 8 characters"
          {...form.getInputProps("newPassword")}
        />
        <PasswordInput
          label="Confirm new password"
          placeholder="Re-enter your new password"
          {...form.getInputProps("confirmPassword")}
        />
        <Button type="submit" fullWidth loading={isPending}>
          Reset Password
        </Button>
      </Stack>
    </form>
  );
}
