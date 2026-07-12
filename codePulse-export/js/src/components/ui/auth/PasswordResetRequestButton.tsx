import { passwordResetRequestFormSchema } from "@/lib/api/schema/auth";
import { usePasswordResetRequestMutation } from "@/lib/api/queries/auth";
import { Button, Stack, TextInput } from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { zodResolver } from "mantine-form-zod-resolver";
import { useState } from "react";
import z from "zod";

export default function PasswordResetRequestButton({
  className,
}: {
  className?: string;
}) {
  const { mutate, isPending } = usePasswordResetRequestMutation();
  const [submitted, setSubmitted] = useState(false);

  const form = useForm({
    validate: zodResolver(passwordResetRequestFormSchema),
    initialValues: {
      email: "",
    },
  });

  const onSubmit = (values: z.infer<typeof passwordResetRequestFormSchema>) => {
    mutate(values, {
      onSuccess: (data) => {
        setSubmitted(true);
        if (!data.success) {
          notifications.show({
            message: data.message,
            color: "red",
          });
        }
      },
      onError: () => {
        notifications.show({
          message: "Sorry, something went wrong. Please try again later.",
          color: "red",
        });
      },
    });
  };

  if (submitted) {
    return (
      <p className={className}>
        If an account with that email exists, a password reset link has been
        sent. Please check your inbox.
      </p>
    );
  }

  return (
    <form onSubmit={form.onSubmit(onSubmit)} className={className}>
      <Stack gap="sm">
        <TextInput
          label="Email"
          placeholder="you@example.com"
          {...form.getInputProps("email")}
        />
        <Button type="submit" fullWidth loading={isPending}>
          Send Reset Link
        </Button>
      </Stack>
    </form>
  );
}
