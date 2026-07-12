import { z } from "zod";

export const runCodeSchema = z.object({
  problemId: z.string().min(1),
  language: z.string().min(1, "Please select a language."),
  sourceCode: z.string().min(1, "Please write some code first."),
  customInput: z.string().optional(),
});

export const submitSolutionSchema = z.object({
  problemId: z.string().min(1),
  language: z.string().min(1, "Please select a language."),
  sourceCode: z.string().min(1, "Please write some code first."),
  lobbyId: z.string().optional(),
});

export const createProblemSchema = z.object({
  title: z.string().min(1, "Please enter a title."),
  slug: z.string().min(1, "Please enter a slug."),
  difficulty: z.enum(["Easy", "Medium", "Hard"], {
    message: "Please select a difficulty.",
  }),
  statement: z.string().min(1, "Please enter a problem statement."),
  constraints: z.string().optional(),
  timeLimitMs: z.number().optional(),
  memoryLimitKb: z.number().optional(),
});

export const createTestCaseSchema = z.object({
  problemId: z.string().min(1),
  input: z.string().min(1, "Please enter input."),
  expectedOutput: z.string().min(1, "Please enter expected output."),
  isHidden: z.boolean().optional(),
  displayOrder: z.number().optional(),
});

export const createStarterCodeSchema = z.object({
  problemId: z.string().min(1),
  language: z.string().min(1, "Please select a language."),
  starterCode: z.string().min(1, "Please enter starter code."),
});

export const createBuggyCodeSchema = z.object({
  problemId: z.string().min(1),
  language: z.string().min(1, "Please select a language."),
  buggyCode: z.string().min(1, "Please enter buggy code."),
});
