import {
  useBuggyCodeQuery,
  useProblemQuery,
  useRunCodeMutation,
  useStarterCodeQuery,
  useSubmissionStatusQuery,
  useSubmitSolutionMutation,
} from "@/lib/api/queries/judge";
import { Api } from "@/lib/api/types";
import Editor from "@monaco-editor/react";
import {
  Badge,
  Button,
  Divider,
  Flex,
  Group,
  Select,
  Stack,
  Text,
  Textarea,
  Title,
} from "@mantine/core";
import { useEffect, useMemo, useState } from "react";

type JudgeLanguage = Api<"ProblemStarterCodeDto">["language"];

const LANGUAGE_OPTIONS: { value: JudgeLanguage; label: string }[] = [
  { value: "PYTHON3", label: "Python 3" },
  { value: "JAVASCRIPT", label: "JavaScript" },
  { value: "TYPESCRIPT", label: "TypeScript" },
  { value: "JAVA", label: "Java" },
  { value: "CPP", label: "C++" },
  { value: "C", label: "C" },
  { value: "GO", label: "Go" },
  { value: "RUST", label: "Rust" },
  { value: "CSHARP", label: "C#" },
  { value: "KOTLIN", label: "Kotlin" },
];

const MONACO_LANGUAGE_MAP: Record<JudgeLanguage, string> = {
  C: "c",
  CPP: "cpp",
  JAVA: "java",
  PYTHON3: "python",
  JAVASCRIPT: "javascript",
  TYPESCRIPT: "typescript",
  GO: "go",
  RUST: "rust",
  CSHARP: "csharp",
  KOTLIN: "kotlin",
};

/**
 * The core Judge0-backed problem-solving workspace: problem statement, Monaco editor with a language
 * selector, a custom-input textarea, Run Code / Submit Solution buttons, and a results panel. Reused
 * both by the standalone problem page and (scoped to a lobby) inside an active duel.
 */
export default function JudgeWorkspace({
  problemId,
  lobbyId,
  onAccepted,
  debugMode,
}: {
  problemId: string;
  lobbyId?: string;
  onAccepted?: () => void;
  /** Debug Challenge mode: loads admin-authored buggy code instead of blank starter code. */
  debugMode?: boolean;
}) {
  const { data: problemData, status: problemStatus } =
    useProblemQuery(problemId);

  const [language, setLanguage] = useState<JudgeLanguage>("PYTHON3");
  const [sourceCode, setSourceCode] = useState("");
  const [customInput, setCustomInput] = useState("");
  const [submissionId, setSubmissionId] = useState<string | undefined>(
    undefined,
  );
  const [editedByUser, setEditedByUser] = useState(false);

  const { data: starterCodeData } = useStarterCodeQuery(
    problemId,
    language,
    !debugMode,
  );
  const { data: buggyCodeData } = useBuggyCodeQuery(
    problemId,
    language,
    !!debugMode,
  );
  const initialCodeData = debugMode ? buggyCodeData : starterCodeData;

  useEffect(() => {
    if (!editedByUser && initialCodeData?.success) {
      setSourceCode(
        "starterCode" in initialCodeData.payload ?
          initialCodeData.payload.starterCode
        : initialCodeData.payload.buggyCode,
      );
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [initialCodeData]);

  const runCodeMutation = useRunCodeMutation();
  const submitSolutionMutation = useSubmitSolutionMutation();

  const shouldPoll =
    !!submissionId &&
    (submitSolutionMutation.data?.success ?
      submitSolutionMutation.data.payload.status === "PENDING" ||
      submitSolutionMutation.data.payload.status === "RUNNING"
    : true);

  const { data: submissionStatusData } = useSubmissionStatusQuery(
    submissionId,
    {
      refetchInterval: shouldPoll ? 2000 : false,
    },
  );

  useEffect(() => {
    if (
      submissionStatusData?.success &&
      submissionStatusData.payload.status === "ACCEPTED"
    ) {
      onAccepted?.();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [submissionStatusData]);

  const problem = problemData?.success ? problemData.payload : undefined;

  const monacoLanguage = useMemo(
    () => MONACO_LANGUAGE_MAP[language],
    [language],
  );

  const handleRun = () => {
    runCodeMutation.mutate({
      problemId,
      language,
      sourceCode,
      customInput: customInput || undefined,
    });
  };

  const handleSubmit = () => {
    submitSolutionMutation.mutate(
      { problemId, language, sourceCode, lobbyId },
      {
        onSuccess: (data) => {
          if (data.success) {
            setSubmissionId(data.payload.id);
          }
        },
      },
    );
  };

  return (
    <Flex direction={{ base: "column", md: "row" }} gap="md" w="100%" h="100%">
      <Stack flex={1} miw={0} gap="sm">
        {problemStatus === "pending" && <Text c="dimmed">Loading problem…</Text>}
        {problemStatus === "error" && (
          <Text c="red">Sorry, something went wrong loading this problem.</Text>
        )}
        {problem && (
          <>
            <Group justify="space-between">
              <Title order={3}>{problem.title}</Title>
              <Group gap="xs">
                {debugMode && <Badge color="orange">Debug Challenge</Badge>}
                <Badge
                  color={
                    problem.difficulty === "Easy" ? "green"
                    : problem.difficulty === "Medium" ? "yellow"
                    : "red"
                  }
                >
                  {problem.difficulty}
                </Badge>
              </Group>
            </Group>
            {debugMode && (
              <Text size="sm" c="orange">
                This solution has an intentional bug. Find it, fix it, and submit.
              </Text>
            )}
            <Text style={{ whiteSpace: "pre-wrap" }}>{problem.statement}</Text>
            {problem.constraints && (
              <>
                <Divider label="Constraints" />
                <Text size="sm" c="dimmed" style={{ whiteSpace: "pre-wrap" }}>
                  {problem.constraints}
                </Text>
              </>
            )}
          </>
        )}
      </Stack>

      <Stack flex={1} miw={0} gap="sm">
        <Group justify="space-between">
          <Select
            data={LANGUAGE_OPTIONS}
            value={language}
            onChange={(value) => {
              if (value) {
                setLanguage(value as JudgeLanguage);
                setEditedByUser(false);
              }
            }}
            allowDeselect={false}
            w={200}
          />
        </Group>

        <div style={{ height: 360, border: "1px solid var(--mantine-color-dark-4)" }}>
          <Editor
            // Forces a full remount when the problem, language, or mode changes, so Monaco
            // never carries over stale internal gutter/line-number state from the previous
            // editor instance (observed as garbled/duplicated line numbers otherwise).
            key={`${problemId}-${language}-${debugMode ? "debug" : "normal"}`}
            height="100%"
            language={monacoLanguage}
            theme="vs-dark"
            value={sourceCode}
            onChange={(value) => {
              setSourceCode(value ?? "");
              setEditedByUser(true);
            }}
            options={{ minimap: { enabled: false }, fontSize: 13 }}
          />
        </div>

        <Textarea
          label="Custom input (optional)"
          placeholder="Falls back to the problem's public sample input if left blank."
          minRows={3}
          value={customInput}
          onChange={(e) => setCustomInput(e.currentTarget.value)}
        />

        <Group>
          <Button
            variant="light"
            loading={runCodeMutation.isPending}
            onClick={handleRun}
          >
            Run Code
          </Button>
          <Button
            loading={submitSolutionMutation.isPending}
            onClick={handleSubmit}
          >
            Submit Solution
          </Button>
        </Group>

        <ResultsPanel
          runResult={runCodeMutation.data}
          submission={submissionStatusData ?? submitSolutionMutation.data}
        />
      </Stack>
    </Flex>
  );
}

function ResultsPanel({
  runResult,
  submission,
}: {
  runResult: ReturnType<typeof useRunCodeMutation>["data"];
  submission: ReturnType<typeof useSubmissionStatusQuery>["data"];
}) {
  if (runResult) {
    if (!runResult.success) {
      return (
        <Text c="red" size="sm">
          {runResult.message}
        </Text>
      );
    }
    const { status, stdout, stderr, compileOutput } = runResult.payload;
    return (
      <Stack gap={4}>
        <Group>
          <Text size="sm" fw={600}>
            Run result:
          </Text>
          <StatusBadge status={status} />
        </Group>
        {stdout && (
          <Text size="sm" ff="monospace" style={{ whiteSpace: "pre-wrap" }}>
            {stdout}
          </Text>
        )}
        {stderr && (
          <Text size="sm" c="red" ff="monospace" style={{ whiteSpace: "pre-wrap" }}>
            {stderr}
          </Text>
        )}
        {compileOutput && (
          <Text size="sm" c="orange" ff="monospace" style={{ whiteSpace: "pre-wrap" }}>
            {compileOutput}
          </Text>
        )}
      </Stack>
    );
  }

  if (submission?.success) {
    const { status, testCasesPassed, testCasesTotal, pointsAwarded } =
      submission.payload;
    return (
      <Stack gap={4}>
        <Group>
          <Text size="sm" fw={600}>
            Submission:
          </Text>
          <StatusBadge status={status} />
        </Group>
        <Text size="sm" c="dimmed">
          {testCasesPassed}/{testCasesTotal} test cases passed
          {status === "ACCEPTED" && pointsAwarded > 0 ?
            ` — +${pointsAwarded} points`
          : ""}
        </Text>
      </Stack>
    );
  }

  return null;
}

function StatusBadge({ status }: { status: string }) {
  const color =
    status === "ACCEPTED" ? "green"
    : status === "PENDING" || status === "RUNNING" ? "gray"
    : "red";
  const label =
    status === "WRONG_ANSWER" ? "TEST CASES FAILED" : status.replaceAll("_", " ");
  return (
    <Badge color={color} variant="light">
      {label}
    </Badge>
  );
}
