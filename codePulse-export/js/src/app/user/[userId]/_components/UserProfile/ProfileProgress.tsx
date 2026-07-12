import { useUserSubmissionsQuery } from "@/lib/api/queries/user";
import { Group, Loader, SimpleGrid, Stack, Text, Title } from "@mantine/core";
import { useMemo } from "react";

const PROGRESS_PAGE_SIZE = 100;

type Difficulty = "Easy" | "Medium" | "Hard";

const DIFFICULTY_COLOR: Record<Difficulty, string> = {
  Easy: "green",
  Medium: "yellow",
  Hard: "red",
};

export default function ProfileProgress({ userId }: { userId: string }) {
  const { data, status } = useUserSubmissionsQuery({
    userId,
    pageSize: PROGRESS_PAGE_SIZE,
  });

  const stats = useMemo(() => {
    if (status !== "success" || !data.success) {
      return null;
    }
    const questions = data.payload.items;
    const accepted = questions.filter((q) => (q.pointsAwarded ?? 0) > 0);
    const byDifficulty: Record<Difficulty, number> = {
      Easy: 0,
      Medium: 0,
      Hard: 0,
    };
    for (const q of questions) {
      const difficulty = q.questionDifficulty as Difficulty;
      if (difficulty in byDifficulty) {
        byDifficulty[difficulty] += 1;
      }
    }
    return {
      totalSolved: questions.length,
      accepted: accepted.length,
      byDifficulty,
      hasNextPage: data.payload.hasNextPage,
    };
  }, [status, data]);

  if (status === "pending") {
    return <Loader size="sm" />;
  }

  if (!stats) {
    return null;
  }

  return (
    <Stack gap="xs" w="100%">
      <Title order={5} ta="center">
        Progress
      </Title>
      <Group justify="center" gap="lg">
        <Stack gap={0} align="center">
          <Text size="xl" fw={700}>
            {stats.totalSolved}
            {stats.hasNextPage ? "+" : ""}
          </Text>
          <Text size="xs" c="dimmed">
            Solved
          </Text>
        </Stack>
        <Stack gap={0} align="center">
          <Text size="xl" fw={700} c="green">
            {stats.accepted}
            {stats.hasNextPage ? "+" : ""}
          </Text>
          <Text size="xs" c="dimmed">
            Accepted
          </Text>
        </Stack>
      </Group>
      <SimpleGrid cols={3} spacing="xs">
        {(["Easy", "Medium", "Hard"] as const).map((difficulty) => (
          <Stack gap={0} align="center" key={difficulty}>
            <Text size="md" fw={600} c={DIFFICULTY_COLOR[difficulty]}>
              {stats.byDifficulty[difficulty]}
            </Text>
            <Text size="xs" c="dimmed">
              {difficulty}
            </Text>
          </Stack>
        ))}
      </SimpleGrid>
    </Stack>
  );
}
