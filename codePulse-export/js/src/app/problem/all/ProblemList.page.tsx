import DocumentTitle from "@/components/ui/title/DocumentTitle";
import { useProblemsQuery } from "@/lib/api/queries/judge";
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
import { useNavigate, useSearchParams } from "react-router-dom";

type Difficulty = "Easy" | "Medium" | "Hard";
type DifficultyFilter = "All" | Difficulty;
type Category = "dsa" | "webdev";

const DIFFICULTY_COLOR: Record<Difficulty, string> = {
  Easy: "green",
  Medium: "yellow",
  Hard: "red",
};

function DifficultyBadge({ difficulty }: { difficulty: string }) {
  const color = DIFFICULTY_COLOR[difficulty as Difficulty] ?? "gray";
  return <Badge color={color}>{difficulty}</Badge>;
}

export default function ProblemListPage() {
  const { data, status } = useProblemsQuery();
  const [searchParams, setSearchParams] = useSearchParams();
  const [difficultyFilter, setDifficultyFilter] =
    useState<DifficultyFilter>("All");

  const category: Category =
    searchParams.get("category") === "webdev" ? "webdev" : "dsa";
  const language = searchParams.get("language");

  const setCategory = (next: Category) => {
    const params = new URLSearchParams(searchParams);
    params.set("category", next);
    setSearchParams(params, { replace: true });
  };

  const allProblems = useMemo(
    () => (status === "success" && data.success ? data.payload : []),
    [status, data],
  );

  const categoryProblems = useMemo(
    () =>
      allProblems.filter(
        (problem) => problem.category === category.toUpperCase(),
      ),
    [allProblems, category],
  );

  const filteredProblems = useMemo(() => {
    if (difficultyFilter === "All") {
      return categoryProblems;
    }
    return categoryProblems.filter(
      (problem) => problem.difficulty === difficultyFilter,
    );
  }, [categoryProblems, difficultyFilter]);

  const counts = useMemo(() => {
    const result: Record<DifficultyFilter, number> = {
      All: categoryProblems.length,
      Easy: 0,
      Medium: 0,
      Hard: 0,
    };
    for (const problem of categoryProblems) {
      const difficulty = problem.difficulty as Difficulty;
      if (difficulty in result) {
        result[difficulty] += 1;
      }
    }
    return result;
  }, [categoryProblems]);

  return (
    <>
      <DocumentTitle title="Problems | CodePulse" />
      <Box p="md" maw={900} mx="auto">
        <Title order={1} mb="md" ta="center">
          Problems
        </Title>
        <Flex justify="center" mb="lg">
          <SegmentedControl
            value={category}
            onChange={(value) => setCategory(value as Category)}
            data={[
              { label: "DSA", value: "dsa" },
              { label: "Web Dev", value: "webdev" },
            ]}
          />
        </Flex>
        {language && (
          <Box ta="center" mb="md">
            <Badge variant="light" size="lg">
              Language: {language}
            </Badge>
          </Box>
        )}
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
                onChange={(value) => setDifficultyFilter(value as DifficultyFilter)}
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
                {categoryProblems.length === 0 ?
                  "No problems available yet."
                : "No problems match this difficulty."}
              </Box>
            )}
            {filteredProblems.length > 0 && (
              <ProblemListStack
                problems={filteredProblems.map((problem) => ({
                  id: problem.id,
                  title: problem.title,
                  difficulty: problem.difficulty,
                }))}
              />
            )}
          </>
        )}
      </Box>
    </>
  );
}

interface ProblemListItem {
  id: string;
  title: string;
  difficulty: string;
}

function ProblemListStack({ problems }: { problems: ProblemListItem[] }) {
  const navigate = useNavigate();

  return (
    <Stack gap={0}>
      {problems.map((problem) => (
        <UnstyledButton
          key={problem.id}
          onClick={() => navigate(`/problem/${problem.id}`)}
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
  );
}
