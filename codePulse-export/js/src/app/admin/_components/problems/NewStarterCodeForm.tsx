import { useCreateStarterCodeMutation } from "@/lib/api/queries/judge";
import { createStarterCodeSchema } from "@/lib/api/schema/judge";
import { Button, Group, Select, Textarea } from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { zodResolver } from "mantine-form-zod-resolver";
import { z } from "zod";

const JUDGE_LANGUAGES = [
  "C",
  "CPP",
  "JAVA",
  "PYTHON3",
  "JAVASCRIPT",
  "TYPESCRIPT",
  "GO",
  "RUST",
  "CSHARP",
  "KOTLIN",
];

export default function NewStarterCodeForm({ problemId }: { problemId: string }) {
  const { mutate, status } = useCreateStarterCodeMutation();

  const form = useForm({
    validate: zodResolver(createStarterCodeSchema),
    initialValues: {
      problemId,
      language: "PYTHON3",
      starterCode: "",
    },
  });

  const onSubmit = (data: z.infer<typeof createStarterCodeSchema>) => {
    mutate(
      { ...data, problemId, language: data.language as never },
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
      <Select
        {...form.getInputProps("language")}
        label="Language"
        data={JUDGE_LANGUAGES}
        error={form.errors.language}
        withAsterisk
        mt="sm"
      />
      <Textarea
        {...form.getInputProps("starterCode")}
        label="Starter code"
        error={form.errors.starterCode}
        withAsterisk
        autosize
        minRows={4}
        mt="sm"
      />
      <Group mt="sm" justify="flex-end">
        <Button
          type="submit"
          size="xs"
          variant="outline"
          disabled={status === "pending"}
          loading={status === "pending"}
        >
          Add starter code
        </Button>
      </Group>
    </form>
  );
}
