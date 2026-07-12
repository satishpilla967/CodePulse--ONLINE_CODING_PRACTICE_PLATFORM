import { registerFormSchema } from "@/lib/api/schema/auth";
import {
  useAdminBootstrapAvailableQuery,
  useRegisterMutation,
} from "@/lib/api/queries/auth";
import {
  Button,
  Checkbox,
  PasswordInput,
  Stack,
  TextInput,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { zodResolver } from "mantine-form-zod-resolver";
import { useNavigate } from "react-router-dom";
import z from "zod";

export default function RegisterButton({ className }: { className?: string }) {
  const { mutate, isPending } = useRegisterMutation();
  const { data: adminBootstrapAvailable } = useAdminBootstrapAvailableQuery();
  const navigate = useNavigate();

  const form = useForm({
    validate: zodResolver(registerFormSchema),
    initialValues: {
      email: "",
      password: "",
      nickname: "",
      becomeAdmin: false,
    },
  });

  const onSubmit = (values: z.infer<typeof registerFormSchema>) => {
    mutate(values, {
      onSuccess: (data) => {
        if (data.success) {
          navigate("/onboarding");
          return;
        }

        notifications.show({
          message: data.message,
          color: "red",
        });
      },
      onError: () => {
        notifications.show({
          message: "Sorry, something went wrong. Please try again later.",
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
        <TextInput
          label="Display name (optional)"
          placeholder="How should we call you?"
          {...form.getInputProps("nickname")}
        />
        <PasswordInput
          label="Password"
          placeholder="At least 8 characters"
          {...form.getInputProps("password")}
        />
        {adminBootstrapAvailable && (
          <Checkbox
            label="Make this account the admin (only available once, for the first account ever created)"
            {...form.getInputProps("becomeAdmin", { type: "checkbox" })}
          />
        )}
        <Button type="submit" fullWidth loading={isPending}>
          Sign Up
        </Button>
      </Stack>
    </form>
  );
}
