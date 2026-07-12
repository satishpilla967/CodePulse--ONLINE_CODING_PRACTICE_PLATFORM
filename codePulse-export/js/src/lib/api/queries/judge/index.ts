import { ApiURL } from "@/lib/api/common/apiURL";
import { Api } from "@/lib/api/types";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

type JudgeLanguage = Api<"ProblemStarterCodeDto">["language"];
type QuestionDifficulty = Api<"ProblemDto">["difficulty"];

// List all problems (public)
export const useProblemsQuery = () => {
  return useQuery({
    queryKey: ApiURL.queryKey("/api/judge/problem", {
      method: "GET",
    }),
    queryFn: listPublicProblems,
  });
};

async function listPublicProblems() {
  const { url, method, res } = ApiURL.create("/api/judge/problem", {
    method: "GET",
  });

  const response = await fetch(url, { method });
  return res(await response.json());
}

// Get problem
export const useProblemQuery = (problemId: string | undefined) => {
  return useQuery({
    queryKey: ApiURL.queryKey("/api/judge/problem/{id}", {
      method: "GET",
      params: { id: problemId ?? "" },
    }),
    queryFn: () => getProblem(problemId as string),
    enabled: !!problemId,
  });
};

async function getProblem(problemId: string) {
  const { url, method, res } = ApiURL.create("/api/judge/problem/{id}", {
    method: "GET",
    params: { id: problemId },
  });

  const response = await fetch(url, { method });
  return res(await response.json());
}

// Get starter code
export const useStarterCodeQuery = (
  problemId: string | undefined,
  language: JudgeLanguage | undefined,
  enabled = true,
) => {
  return useQuery({
    queryKey: ApiURL.queryKey("/api/judge/problem/{id}/starter-code/{language}", {
      method: "GET",
      params: { id: problemId ?? "", language: language ?? "PYTHON3" },
    }),
    queryFn: () => getStarterCode(problemId as string, language as JudgeLanguage),
    enabled: enabled && !!problemId && !!language,
  });
};

async function getStarterCode(problemId: string, language: JudgeLanguage) {
  const { url, method, res } = ApiURL.create(
    "/api/judge/problem/{id}/starter-code/{language}",
    {
      method: "GET",
      params: { id: problemId, language },
    },
  );

  const response = await fetch(url, { method });
  return res(await response.json());
}

// List problems that have a Debug Challenge (buggy-code) available (public)
export const useDebugChallengesQuery = () => {
  return useQuery({
    queryKey: ApiURL.queryKey("/api/judge/debug-challenges", {
      method: "GET",
    }),
    queryFn: listDebugChallenges,
  });
};

async function listDebugChallenges() {
  const { url, method, res } = ApiURL.create("/api/judge/debug-challenges", {
    method: "GET",
  });

  const response = await fetch(url, { method });
  return res(await response.json());
}

// Get buggy (Debug Challenge) code
export const useBuggyCodeQuery = (
  problemId: string | undefined,
  language: JudgeLanguage | undefined,
  enabled = true,
) => {
  return useQuery({
    queryKey: ApiURL.queryKey("/api/judge/problem/{id}/buggy-code/{language}", {
      method: "GET",
      params: { id: problemId ?? "", language: language ?? "PYTHON3" },
    }),
    queryFn: () => getBuggyCode(problemId as string, language as JudgeLanguage),
    enabled: enabled && !!problemId && !!language,
  });
};

async function getBuggyCode(problemId: string, language: JudgeLanguage) {
  const { url, method, res } = ApiURL.create(
    "/api/judge/problem/{id}/buggy-code/{language}",
    {
      method: "GET",
      params: { id: problemId, language },
    },
  );

  const response = await fetch(url, { method });
  return res(await response.json());
}

// Run code
export const useRunCodeMutation = () => {
  return useMutation({
    mutationFn: runCode,
  });
};

async function runCode(body: {
  problemId: string;
  language: JudgeLanguage;
  sourceCode: string;
  customInput?: string;
}) {
  const { url, method, req, res } = ApiURL.create("/api/judge/run", {
    method: "POST",
  });

  const response = await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: req({
      problemId: body.problemId,
      language: body.language,
      sourceCode: body.sourceCode,
      customInput: body.customInput ?? null,
    }),
  });

  return res(await response.json());
}

// Submit solution
export const useSubmitSolutionMutation = () => {
  return useMutation({
    mutationFn: submitSolution,
  });
};

async function submitSolution(body: {
  problemId: string;
  language: JudgeLanguage;
  sourceCode: string;
  lobbyId?: string;
}) {
  const { url, method, req, res } = ApiURL.create("/api/judge/submit", {
    method: "POST",
  });

  const response = await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: req({
      problemId: body.problemId,
      language: body.language,
      sourceCode: body.sourceCode,
      lobbyId: body.lobbyId ?? null,
    }),
  });

  return res(await response.json());
}

// Poll submission status
export const useSubmissionStatusQuery = (
  submissionId: string | undefined,
  options?: { refetchInterval?: number | false },
) => {
  return useQuery({
    queryKey: ApiURL.queryKey("/api/judge/submission/{id}", {
      method: "GET",
      params: { id: submissionId ?? "" },
    }),
    queryFn: () => getSubmission(submissionId as string),
    enabled: !!submissionId,
    refetchInterval: options?.refetchInterval ?? 2000,
  });
};

async function getSubmission(submissionId: string) {
  const { url, method, res } = ApiURL.create("/api/judge/submission/{id}", {
    method: "GET",
    params: { id: submissionId },
  });

  const response = await fetch(url, { method });
  return res(await response.json());
}

// ---------------------------------------------------------------------------
// Admin CRUD
// ---------------------------------------------------------------------------

// List all problems (admin)
export const useProblemsListQuery = () => {
  return useQuery({
    queryKey: ApiURL.queryKey("/api/admin/judge/problem", {
      method: "GET",
    }),
    queryFn: getAllProblems,
  });
};

async function getAllProblems() {
  const { url, method, res } = ApiURL.create("/api/admin/judge/problem", {
    method: "GET",
  });

  const response = await fetch(url, { method });
  return res(await response.json());
}

// Get single problem, including hidden test cases and all starter code (admin)
export const useProblemAdminQuery = (problemId: string | undefined) => {
  return useQuery({
    queryKey: ApiURL.queryKey("/api/admin/judge/problem/{id}", {
      method: "GET",
      params: { id: problemId ?? "" },
    }),
    queryFn: () => getProblemAdmin(problemId as string),
    enabled: !!problemId,
  });
};

async function getProblemAdmin(problemId: string) {
  const { url, method, res } = ApiURL.create("/api/admin/judge/problem/{id}", {
    method: "GET",
    params: { id: problemId },
  });

  const response = await fetch(url, { method });
  return res(await response.json());
}

// Create problem
export const useCreateProblemMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createProblem,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ApiURL.prefix("/api/admin/judge/problem"),
      });
    },
  });
};

async function createProblem(body: {
  title: string;
  slug: string;
  difficulty: QuestionDifficulty;
  statement: string;
  constraints?: string;
  timeLimitMs?: number;
  memoryLimitKb?: number;
}) {
  const { url, method, req, res } = ApiURL.create("/api/admin/judge/problem/create", {
    method: "POST",
  });

  const response = await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: req({
      title: body.title,
      slug: body.slug,
      difficulty: body.difficulty,
      statement: body.statement,
      constraints: body.constraints ?? null,
      timeLimitMs: body.timeLimitMs ?? null,
      memoryLimitKb: body.memoryLimitKb ?? null,
    }),
  });

  return res(await response.json());
}

// Delete problem
export const useDeleteProblemMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteProblem,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ApiURL.prefix("/api/admin/judge/problem"),
      });
    },
  });
};

async function deleteProblem(problemId: string) {
  const { url, method, res } = ApiURL.create("/api/admin/judge/problem/{id}", {
    method: "DELETE",
    params: { id: problemId },
  });

  const response = await fetch(url, { method });
  return res(await response.json());
}

// Create test case
export const useCreateTestCaseMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createTestCase,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ApiURL.prefix("/api/admin/judge/problem"),
      });
    },
  });
};

async function createTestCase(body: {
  problemId: string;
  input: string;
  expectedOutput: string;
  isHidden?: boolean;
  displayOrder?: number;
}) {
  const { url, method, req, res } = ApiURL.create("/api/admin/judge/test-case/create", {
    method: "POST",
  });

  const response = await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: req({
      problemId: body.problemId,
      input: body.input,
      expectedOutput: body.expectedOutput,
      isHidden: body.isHidden ?? true,
      displayOrder: body.displayOrder ?? 0,
    }),
  });

  return res(await response.json());
}

// Delete test case
export const useDeleteTestCaseMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteTestCase,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ApiURL.prefix("/api/admin/judge/problem"),
      });
    },
  });
};

async function deleteTestCase(testCaseId: string) {
  const { url, method, res } = ApiURL.create("/api/admin/judge/test-case/{id}", {
    method: "DELETE",
    params: { id: testCaseId },
  });

  const response = await fetch(url, { method });
  return res(await response.json());
}

// Create starter code
export const useCreateStarterCodeMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createStarterCode,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ApiURL.prefix("/api/admin/judge/problem"),
      });
    },
  });
};

async function createStarterCode(body: {
  problemId: string;
  language: JudgeLanguage;
  starterCode: string;
}) {
  const { url, method, req, res } = ApiURL.create("/api/admin/judge/starter-code/create", {
    method: "POST",
  });

  const response = await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: req({
      problemId: body.problemId,
      language: body.language,
      starterCode: body.starterCode,
    }),
  });

  return res(await response.json());
}

// Create buggy code (Debug Challenge)
export const useCreateBuggyCodeMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createBuggyCode,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ApiURL.prefix("/api/admin/judge/problem"),
      });
    },
  });
};

async function createBuggyCode(body: {
  problemId: string;
  language: JudgeLanguage;
  buggyCode: string;
}) {
  const { url, method, req, res } = ApiURL.create("/api/admin/judge/buggy-code/create", {
    method: "POST",
  });

  const response = await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: req({
      problemId: body.problemId,
      language: body.language,
      buggyCode: body.buggyCode,
    }),
  });

  return res(await response.json());
}
