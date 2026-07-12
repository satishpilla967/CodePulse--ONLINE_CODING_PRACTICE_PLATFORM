import { useCreateTestCaseMutation } from "@/lib/api/queries/judge";
import { createTestCaseSchema } from "@/lib/api/schema/judge";
import { Button, Group, Switch, Textarea } from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { zodResolver } from "mantine-form-zod-resolver";
import { z } from "zod";

export default function NewTestCaseForm({ problemId }: { problemId: string }) {
  const { mutate, status } = useCreateTestCaseMutation();

  const form = useForm({
    validate: zodResolver(createTestCaseSchema),
    initialValues: {
      problemId,
      input: "",
      expectedOutput: "",
      isHidden: true,
      displayOrder: 0,
    },
  });

  const onSubmit = (data: z.infer<typeof createTestCaseSchema>) => {
    mutate(
      { ...data, problemId },
      {
        onSuccess: (data) => {
          notifications.show({
            message: data.message,
            color: data.success ? undefined : "red",
          });
          if (data.success) {
            form.reset();
          }
        },
      },
    );
  };

  return (
    <form onSubmit={form.onSubmit(onSubmit)}>
      <Textarea
        {...form.getInputProps("input")}
        label="Input"
        error={form.errors.input}
        withAsterisk
        autosize
        minRows={2}
        mt="sm"
      />
      <Textarea
        {...form.getInputProps("expectedOutput")}
        label="Expected output"
        error={form.errors.expectedOutput}
        withAsterisk
        autosize
        minRows={2}
        mt="sm"
      />
      <Group mt="sm" justify="space-between" align="center">
        <Switch
          {...form.getInputProps("isHidden", { type: "checkbox" })}
          label="Hidden test case"
        />
        <Button
          type="submit"
          size="xs"
          variant="outline"
          disabled={status === "pending"}
          loading={status === "pending"}
        >
          Add test case
        </Button>
      </Group>
    </form>
  );
}
