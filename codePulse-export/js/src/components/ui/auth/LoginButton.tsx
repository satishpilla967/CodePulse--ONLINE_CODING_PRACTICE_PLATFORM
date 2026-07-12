import { loginFormSchema } from "@/lib/api/schema/auth";
import { useLoginMutation } from "@/lib/api/queries/auth";
import { Button, PasswordInput, Stack, TextInput } from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { zodResolver } from "mantine-form-zod-resolver";
import { useNavigate } from "react-router-dom";
import z from "zod";

export default function LoginButton({ className }: { className?: string }) {
  const { mutate, isPending } = useLoginMutation();
  const navigate = useNavigate();

  const form = useForm({
    validate: zodResolver(loginFormSchema),
    initialValues: {
      email: "",
      password: "",
    },
  });

  const onSubmit = (values: z.infer<typeof loginFormSchema>) => {
    mutate(values, {
      onSuccess: (data) => {
        if (data.success) {
          navigate("/leaderboard");
          return;
        }

        notifications.show({
          message: data.message,
          color: "red",
        });
      },
      onError: () => {
        notifications.show({
          message: "Invalid email or password.",
          color: "red",
        });
      },
    });
  };

  return (
    <form onSubmit={form.onSubmit(onSubmit)} className={className}>
      <Stack gap="sm">
        <TextInput
          label="Email"
          placeholder="you@example.com"
          {...form.getInputProps("email")}
        />
        <PasswordInput
          label="Password"
          placeholder="Your password"
          {...form.getInputProps("password")}
        />
        <Button type="submit" fullWidth loading={isPending}>
          Log In
        </Button>
      </Stack>
    </form>
  );
}
