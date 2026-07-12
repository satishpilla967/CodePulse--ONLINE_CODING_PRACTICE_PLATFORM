import { useCreateProblemMutation } from "@/lib/api/queries/judge";
import { createProblemSchema } from "@/lib/api/schema/judge";
import { Button, Modal, Select, TextInput, Textarea } from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { zodResolver } from "mantine-form-zod-resolver";
import { useState } from "react";
import { z } from "zod";

export default function NewProblemModal() {
  const [isModalOpen, setModalOpen] = useState(false);
  const { mutate, status } = useCreateProblemMutation();

  const form = useForm({
    validate: zodResolver(createProblemSchema),
    initialValues: {
      title: "",
      slug: "",
      difficulty: "Easy" as "Easy" | "Medium" | "Hard",
      statement: "",
      constraints: "",
      timeLimitMs: 2000,
      memoryLimitKb: 128000,
    },
    transformValues: (values) => ({
      ...values,
      timeLimitMs: Number(values.timeLimitMs),
      memoryLimitKb: Number(values.memoryLimitKb),
    }),
  });

  const toggleModal = () => {
    setModalOpen((prev) => !prev);
  };

  const onSubmit = (data: z.infer<typeof createProblemSchema>) => {
    mutate(
      { ...data },
      {
        onSuccess: (data) => {
          notifications.show({
            message: data.message,
            color: data.success ? undefined : "red",
          });
          if (data.success) {
            form.reset();
            setModalOpen(false);
          }
        },
      },
    );
  };

  return (
    <>
      <Button onClick={toggleModal} top={12}>
        New Problem
      </Button>
      <Modal
        opened={isModalOpen}
        onClose={toggleModal}
        title="Create new problem"
        size="lg"
      >
        <form onSubmit={form.onSubmit(onSubmit)}>
          <TextInput
            {...form.getInputProps("title")}
            label="Title"
            error={form.errors.title}
            withAsterisk
            mb={"sm"}
          />
          <TextInput
            {...form.getInputProps("slug")}
            label="Slug"
            error={form.errors.slug}
            withAsterisk
            mb={"sm"}
          />
          <Select
            {...form.getInputProps("difficulty")}
            label="Difficulty"
            data={["Easy", "Medium", "Hard"]}
            error={form.errors.difficulty}
            withAsterisk
            mb={"sm"}
          />
          <Textarea
            {...form.getInputProps("statement")}
            label="Problem statement"
            error={form.errors.statement}
            withAsterisk
            autosize
            minRows={4}
            mb={"sm"}
          />
          <Textarea
            {...form.getInputProps("constraints")}
            label="Constraints"
            error={form.errors.constraints}
            autosize
            minRows={2}
            mb={"sm"}
          />
          <TextInput
            {...form.getInputProps("timeLimitMs")}
            type="number"
            label="Time limit (ms)"
            error={form.errors.timeLimitMs}
            mb={"sm"}
          />
          <TextInput
            {...form.getInputProps("memoryLimitKb")}
            type="number"
            label="Memory limit (KB)"
            error={form.errors.memoryLimitKb}
            mb={"sm"}
          />
          <Button
            type="submit"
            size="xs"
            mt="sm"
            variant="outline"
            disabled={status === "pending"}
            loading={status === "pending"}
          >
            Submit
          </Button>
        </form>
      </Modal>
    </>
  );
}
