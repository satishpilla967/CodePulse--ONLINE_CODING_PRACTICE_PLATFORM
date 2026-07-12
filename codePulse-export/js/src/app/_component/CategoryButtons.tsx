import { Button, Group } from "@mantine/core";
import { useNavigate } from "react-router-dom";

export default function CategoryButtons() {
  const navigate = useNavigate();

  return (
    <Group justify="center" mt="xl" gap="md">
      <Button
        size="md"
        variant="filled"
        onClick={() => navigate("/problem/all?category=dsa")}
      >
        DSA Problems
      </Button>
      <Button
        size="md"
        variant="outline"
        onClick={() => navigate("/problem/all?category=webdev")}
      >
        Web Dev Problems
      </Button>
    </Group>
  );
}
