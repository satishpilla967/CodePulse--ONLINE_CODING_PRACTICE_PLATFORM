import DeleteProblemModal from "@/app/admin/_components/problems/DeleteProblemModal";
import ProblemDetailManager from "@/app/admin/_components/problems/ProblemDetailManager";
import { useProblemsListQuery } from "@/lib/api/queries/judge";
import { Badge, Button, Collapse, Table, Text } from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";

function DifficultyBadge({ difficulty }: { difficulty: string }) {
  const color =
    difficulty === "Easy" ? "green"
    : difficulty === "Medium" ? "yellow"
    : "red";
  return <Badge color={color}>{difficulty}</Badge>;
}

function ProblemRow({ problem }: { problem: { id: string; title: string; slug: string; difficulty: string } }) {
  const [detailsOpened, { toggle: toggleDetails }] = useDisclosure(false);
  const [deleteOpened, { open: openDelete, close: closeDelete }] = useDisclosure(false);

  return (
    <>
      <Table.Tr>
        <Table.Td>{problem.title}</Table.Td>
        <Table.Td>{problem.slug}</Table.Td>
        <Table.Td>
          <DifficultyBadge difficulty={problem.difficulty} />
        </Table.Td>
        <Table.Td>
          <Button size="xs" variant="outline" onClick={toggleDetails} mr="xs">
            {detailsOpened ? "Hide" : "Manage"}
          </Button>
          <Button size="xs" variant="outline" color="red" onClick={openDelete}>
            Delete
          </Button>
        </Table.Td>
      </Table.Tr>
      <Table.Tr>
        <Table.Td colSpan={4} p={0}>
          <Collapse in={detailsOpened}>
            {detailsOpened && <ProblemDetailManager problemId={problem.id} />}
          </Collapse>
        </Table.Td>
      </Table.Tr>
      <DeleteProblemModal
        id={problem.id}
        title={problem.title}
        opened={deleteOpened}
        onClose={closeDelete}
      />
    </>
  );
}

export default function ProblemAdminList() {
  const { data, status } = useProblemsListQuery();

  if (status === "pending") {
    return <Text>Loading problems...</Text>;
  }

  if (status === "error") {
    return <Text>Error loading problems.</Text>;
  }

  if (!data.success) {
    return <Text>{data.message}</Text>;
  }

  const problems = data.payload;

  if (problems.length === 0) {
    return <Text c="dimmed">No problems yet. Create one above.</Text>;
  }

  return (
    <Table striped highlightOnHover>
      <Table.Thead>
        <Table.Tr>
          <Table.Th>Title</Table.Th>
          <Table.Th>Slug</Table.Th>
          <Table.Th>Difficulty</Table.Th>
          <Table.Th>Actions</Table.Th>
        </Table.Tr>
      </Table.Thead>
      <Table.Tbody>
        {problems.map((problem) => (
          <ProblemRow key={problem.id} problem={problem} />
        ))}
      </Table.Tbody>
    </Table>
  );
}
