import { useUpdateProfileMutation } from "@/lib/api/queries/user";
import { resizeImageToDataUri } from "@/lib/helper/resizeImageToDataUri";
import {
  Avatar,
  Button,
  Center,
  Divider,
  FileButton,
  Modal,
  Stack,
  TextInput,
} from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { useState } from "react";

interface EditProfileModalProps {
  opened: boolean;
  onClose: () => void;
  initialNickname: string;
  initialProfileUrl: string;
}

export default function EditProfileModal({
  opened,
  onClose,
  initialNickname,
  initialProfileUrl,
}: EditProfileModalProps) {
  const [nickname, setNickname] = useState(initialNickname);
  const [profileUrl, setProfileUrl] = useState(initialProfileUrl);
  const [isProcessingImage, setIsProcessingImage] = useState(false);
  const { mutate, isPending } = useUpdateProfileMutation();

  const handleFileSelect = async (file: File | null) => {
    if (!file) {
      return;
    }
    if (!file.type.startsWith("image/")) {
      notifications.show({ message: "Please choose an image file.", color: "red" });
      return;
    }
    setIsProcessingImage(true);
    try {
      const dataUri = await resizeImageToDataUri(file);
      setProfileUrl(dataUri);
    } catch {
      notifications.show({
        message: "Failed to process that image. Please try a different file.",
        color: "red",
      });
    } finally {
      setIsProcessingImage(false);
    }
  };

  const handleSave = () => {
    mutate(
      {
        nickname: nickname.trim() || undefined,
        profileUrl: profileUrl.trim() || undefined,
      },
      {
        onSuccess: (data) => {
          if (data.success) {
            notifications.show({
              message: "Profile updated!",
              color: "green",
            });
            onClose();
          } else {
            notifications.show({ message: data.message, color: "red" });
          }
        },
        onError: () => {
          notifications.show({
            message: "Failed to update profile. Please try again later.",
            color: "red",
          });
        },
      },
    );
  };

  return (
    <Modal opened={opened} onClose={onClose} title="Edit Profile" centered>
      <Stack gap="md">
        <TextInput
          label="Display name"
          placeholder="Your display name"
          value={nickname}
          onChange={(e) => setNickname(e.currentTarget.value)}
          maxLength={64}
        />
        <Center>
          <Avatar src={profileUrl || undefined} size={80} radius="md" />
        </Center>
        <FileButton onChange={handleFileSelect} accept="image/*">
          {(props) => (
            <Button {...props} variant="light" loading={isProcessingImage} fullWidth>
              Upload picture
            </Button>
          )}
        </FileButton>
        <Divider label="or" />
        <TextInput
          label="Profile picture URL"
          placeholder="https://example.com/avatar.png"
          value={profileUrl}
          onChange={(e) => setProfileUrl(e.currentTarget.value)}
        />
        <Button onClick={handleSave} loading={isPending} fullWidth>
          Save
        </Button>
      </Stack>
    </Modal>
  );
}
