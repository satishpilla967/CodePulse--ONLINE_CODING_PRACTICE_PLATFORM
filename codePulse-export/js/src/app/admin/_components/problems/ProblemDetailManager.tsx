import NewBuggyCodeForm from "@/app/admin/_components/problems/NewBuggyCodeForm";
import NewStarterCodeForm from "@/app/admin/_components/problems/NewStarterCodeForm";
import NewTestCaseForm from "@/app/admin/_components/problems/NewTestCaseForm";
import {
  useDeleteTestCaseMutation,
  useProblemAdminQuery,
} from "@/lib/api/queries/judge";
import { Badge, Box, Button, Divider, Stack, Table, Text, Title } from "@mantine/core";
import { notifications } from "@mantine/notifications";

export default function ProblemDetailManager({ problemId }: { problemId: string }) {
  const { data, status } = useProblemAdminQuery(problemId);
  const { mutate: deleteTestCase, status: deleteStatus } = useDeleteTestCaseMutation();

  if (status === "pending") {
    return (
      <Box p="md">
        <Text>Loading details...</Text>
      </Box>
    );
  }

  if (status === "error" || !data.success) {
    return (
      <Box p="md">
        <Text c="red">Failed to load problem details.</Text>
      </Box>
    );
  }

  const problem = data.payload;

  const onDeleteTestCase = (testCaseId: string) => {
    deleteTestCase(testCaseId, {
      onSuccess: (data) => {
        notifications.show({
          message: data.message,
          color: data.success ? undefined : "red",
        });
      },
    });
  };

  return (
    <Box p="md" bg="var(--mantine-color-default-hover)">
      <Stack gap="md">
        <div>
          <Title order={5}>Test Cases</Title>
          {problem.testCases && problem.testCases.length > 0 ?
            <Table>
              <Table.Thead>
                <Table.Tr>
                  <Table.Th>Input</Table.Th>
                  <Table.Th>Expected Output</Table.Th>
                  <Table.Th>Hidden</Table.Th>
                  <Table.Th>Order</Table.Th>
                  <Table.Th></Table.Th>
                </Table.Tr>
              </Table.Thead>
              <Table.Tbody>
                {problem.testCases.map((testCase) => (
                  <Table.Tr key={testCase.id}>
                    <Table.Td>
                      <Text truncate="end" maw={200}>
                        {testCase.input}
                      </Text>
                    </Table.Td>
                    <Table.Td>
                      <Text truncate="end" maw={200}>
                        {testCase.expectedOutput}
                      </Text>
                    </Table.Td>
                    <Table.Td>
                      <Badge color={testCase.isHidden ? "gray" : "blue"}>
                        {testCase.isHidden ? "Hidden" : "Public"}
                      </Badge>
                    </Table.Td>
                    <Table.Td>{testCase.displayOrder}</Table.Td>
                    <Table.Td>
                      <Button
                        size="xs"
                        color="red"
                        variant="subtle"
                        disabled={deleteStatus === "pending"}
                        onClick={() => onDeleteTestCase(testCase.id)}
                      >
                        Delete
                      </Button>
                    </Table.Td>
                  </Table.Tr>
                ))}
              </Table.Tbody>
            </Table>
          : <Text c="dimmed">No test cases yet.</Text>}
          <NewTestCaseForm problemId={problemId} />
        </div>

        <Divider />

        <div>
          <Title order={5}>Starter Code</Title>
          {problem.starterCode && problem.starterCode.length > 0 ?
            <Stack gap="xs">
              {problem.starterCode.map((sc) => (
                <Box key={sc.language}>
                  <Badge mr="xs">{sc.language}</Badge>
                  <Text component="pre" size="xs" style={{ whiteSpace: "pre-wrap" }}>
                    {sc.starterCode}
                  </Text>
                </Box>
              ))}
            </Stack>
          : <Text c="dimmed">No starter code yet.</Text>}
          <NewStarterCodeForm problemId={problemId} />
        </div>

        <Divider />

        <div>
          <Title order={5}>Debug Challenge (Buggy Code)</Title>
          {problem.buggyCode && problem.buggyCode.length > 0 ?
            <Stack gap="xs">
              {problem.buggyCode.map((bc) => (
                <Box key={bc.language}>
                  <Badge mr="xs" color="orange">
                    {bc.language}
                  </Badge>
                  <Text component="pre" size="xs" style={{ whiteSpace: "pre-wrap" }}>
                    {bc.buggyCode}
                  </Text>
                </Box>
              ))}
            </Stack>
          : <Text c="dimmed">No debug challenge yet.</Text>}
          <NewBuggyCodeForm problemId={problemId} />
        </div>
      </Stack>
    </Box>
  );
}
