import { useDeleteProblemMutation } from "@/lib/api/queries/judge";
import { Modal, Text, Button } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { FormEvent } from "react";

export default function DeleteProblemModal({
  id,
  title,
  opened,
  onClose,
}: {
  id: string;
  title: string;
  opened: boolean;
  onClose: () => void;
}) {
  const { mutate, status } = useDeleteProblemMutation();

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    e.stopPropagation();
    mutate(id, {
      onSuccess: (data) => {
        notifications.show({
          message: data.message,
          color: data.success ? undefined : "red",
        });
        onClose();
      },
    });
  };

  return (
    <Modal opened={opened} onClose={onClose} title="Delete Problem">
      <form onSubmit={onSubmit}>
        <Text mt={12}>
          Are you sure you want to delete &quot;{title}&quot;? This also
          deletes its test cases, starter code, and submissions.
        </Text>
        <Button
          type="submit"
          size="xs"
          mt="sm"
          variant="outline"
          color="red"
          disabled={status === "pending"}
          loading={status === "pending"}
        >
          Delete
        </Button>
      </form>
    </Modal>
  );
}
