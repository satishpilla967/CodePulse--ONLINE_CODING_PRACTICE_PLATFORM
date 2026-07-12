import DocumentTitle from "@/components/ui/title/DocumentTitle";
import { useDebugChallengesQuery } from "@/lib/api/queries/judge";
import {
  Badge,
  Box,
  Flex,
  Group,
  Loader,
  SegmentedControl,
  Stack,
  Text,
  Title,
  UnstyledButton,
} from "@mantine/core";
import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

type Difficulty = "Easy" | "Medium" | "Hard";
type DifficultyFilter = "All" | Difficulty;

const DIFFICULTY_COLOR: Record<Difficulty, string> = {
  Easy: "green",
  Medium: "yellow",
  Hard: "red",
};

function DifficultyBadge({ difficulty }: { difficulty: string }) {
  const color = DIFFICULTY_COLOR[difficulty as Difficulty] ?? "gray";
  return <Badge color={color}>{difficulty}</Badge>;
}

export default function DebugChallengesListPage() {
  const { data, status } = useDebugChallengesQuery();
  const navigate = useNavigate();
  const [difficultyFilter, setDifficultyFilter] =
    useState<DifficultyFilter>("All");

  const problems = status === "success" && data.success ? data.payload : [];

  const filteredProblems = useMemo(() => {
    if (difficultyFilter === "All") {
      return problems;
    }
    return problems.filter((problem) => problem.difficulty === difficultyFilter);
  }, [problems, difficultyFilter]);

  const counts = useMemo(() => {
    const result: Record<DifficultyFilter, number> = {
      All: problems.length,
      Easy: 0,
      Medium: 0,
      Hard: 0,
    };
    for (const problem of problems) {
      const difficulty = problem.difficulty as Difficulty;
      if (difficulty in result) {
        result[difficulty] += 1;
      }
    }
    return result;
  }, [problems]);

  return (
    <>
      <DocumentTitle title="Debug Challenges | CodePulse" />
      <Box p="md" maw={900} mx="auto">
        <Title order={1} mb="xs" ta="center">
          Debug Challenges
        </Title>
        <Text ta="center" c="dimmed" mb="md">
          Each challenge starts from a solution with an intentional bug. Find it, fix it, submit.
        </Text>

        {status === "pending" && (
          <Box ta="center">
            <Loader />
          </Box>
        )}
        {status === "error" && <Box ta="center">Something went wrong.</Box>}
        {status === "success" && !data.success && (
          <Box ta="center">{data.message}</Box>
        )}

        {status === "success" && data.success && (
          <>
            <Flex justify="center" mb="lg">
              <SegmentedControl
                value={difficultyFilter}
                onChange={(value) =>
                  setDifficultyFilter(value as DifficultyFilter)
                }
                data={(["All", "Easy", "Medium", "Hard"] as const).map(
                  (difficulty) => ({
                    label: `${difficulty} (${counts[difficulty]})`,
                    value: difficulty,
                  }),
                )}
              />
            </Flex>

            {filteredProblems.length === 0 && (
              <Box ta="center" c="dimmed">
                {problems.length === 0 ?
                  "No debug challenges available yet."
                : "No debug challenges match this difficulty."}
              </Box>
            )}

            {filteredProblems.length > 0 && (
              <Stack gap={0}>
                {filteredProblems.map((problem) => (
                  <UnstyledButton
                    key={problem.id}
                    onClick={() => navigate(`/debug-challenges/${problem.id}`)}
                    px="md"
                    py="sm"
                    className="transition-all hover:bg-dark-500!"
                    style={{
                      borderBottom: "1px solid var(--mantine-color-dark-4)",
                    }}
                  >
                    <Group justify="space-between" wrap="nowrap">
                      <Text fw={500}>{problem.title}</Text>
                      <DifficultyBadge difficulty={problem.difficulty} />
                    </Group>
                  </UnstyledButton>
                ))}
              </Stack>
            )}
          </>
        )}
      </Box>
    </>
  );
}
