package org.patinanetwork.codepulse.common.judge;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmission;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmissionResult;
import org.patinanetwork.codepulse.common.db.models.judge.Problem;
import org.patinanetwork.codepulse.common.db.models.judge.SubmissionStatus;
import org.patinanetwork.codepulse.common.db.models.judge.TestCase;
import org.patinanetwork.codepulse.common.db.models.leaderboard.Leaderboard;
import org.patinanetwork.codepulse.common.db.models.user.User;
import org.patinanetwork.codepulse.common.db.models.user.UserWithScore;
import org.patinanetwork.codepulse.common.db.repos.judge.JudgeSubmissionRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.JudgeSubmissionResultRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.ProblemRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.TestCaseRepository;
import org.patinanetwork.codepulse.common.db.repos.leaderboard.LeaderboardRepository;
import org.patinanetwork.codepulse.common.db.repos.user.UserRepository;
import org.patinanetwork.codepulse.common.db.repos.user.options.UserFilterOptions;
import org.patinanetwork.codepulse.common.judge.score.JudgeScoreCalculator;
import org.patinanetwork.codepulse.common.judge0.Judge0LanguageMapper;
import org.patinanetwork.codepulse.common.judge0.Judge0Submission;
import org.patinanetwork.codepulse.common.judge0.throttled.ThrottledJudge0Client;
import org.springframework.stereotype.Component;

/**
 * Business-logic orchestrator for the internal Judge0-backed judge, following the {@code DuelManager}/
 * {@code SubmissionsHandler} "manager/handler" convention used elsewhere in {@code common/}.
 */
@Component
@Slf4j
public class Judge0Service {

    /** Priority order used by {@link #computeVerdict(List)} — first non-accepted category wins. */
    private static final List<SubmissionStatus> VERDICT_PRIORITY = List.of(
            SubmissionStatus.COMPILE_ERROR,
            SubmissionStatus.INTERNAL_ERROR,
            SubmissionStatus.RUNTIME_ERROR,
            SubmissionStatus.TIME_LIMIT_EXCEEDED,
            SubmissionStatus.MEMORY_LIMIT_EXCEEDED,
            SubmissionStatus.WRONG_ANSWER);

    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final JudgeSubmissionRepository judgeSubmissionRepository;
    private final JudgeSubmissionResultRepository judgeSubmissionResultRepository;
    private final ThrottledJudge0Client judge0Client;
    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;

    public Judge0Service(
            final ProblemRepository problemRepository,
            final TestCaseRepository testCaseRepository,
            final JudgeSubmissionRepository judgeSubmissionRepository,
            final JudgeSubmissionResultRepository judgeSubmissionResultRepository,
            final ThrottledJudge0Client judge0Client,
            final LeaderboardRepository leaderboardRepository,
            final UserRepository userRepository) {
        this.problemRepository = problemRepository;
        this.testCaseRepository = testCaseRepository;
        this.judgeSubmissionRepository = judgeSubmissionRepository;
        this.judgeSubmissionResultRepository = judgeSubmissionResultRepository;
        this.judge0Client = judge0Client;
        this.leaderboardRepository = leaderboardRepository;
        this.userRepository = userRepository;
    }

    public static SubmissionStatus judge0StatusToSubmissionStatus(final int statusId) {
        // Judge0 status IDs: 1=In Queue, 2=Processing, 3=Accepted, 4=Wrong Answer, 5=TLE,
        // 6=Compile Error, 7-12=various runtime errors, 13=Internal Error, 14=Exec Format Error.
        return switch (statusId) {
            case 1, 2 -> SubmissionStatus.RUNNING;
            case 3 -> SubmissionStatus.ACCEPTED;
            case 4 -> SubmissionStatus.WRONG_ANSWER;
            case 5 -> SubmissionStatus.TIME_LIMIT_EXCEEDED;
            case 6 -> SubmissionStatus.COMPILE_ERROR;
            case 7, 8, 9, 10, 11, 12 -> SubmissionStatus.RUNTIME_ERROR;
            default -> SubmissionStatus.INTERNAL_ERROR;
        };
    }

    /** "Run Code" — a single ad-hoc execution, never persisted, never awards points. */
    public Judge0Submission runCode(final Problem problem, final JudgeLanguage language, final String sourceCode, final String customInput) {
        String stdin = customInput;
        // Only known when falling back to the problem's own sample test case; a user-supplied custom
        // input has no known correct answer, so we can't ask Judge0 to validate it (expectedOutput stays
        // null and Judge0 just reports whether the program ran, not whether it was "right").
        String expectedOutput = null;
        if (stdin == null) {
            List<TestCase> publicTestCases = testCaseRepository.getPublicTestCasesByProblemId(problem.getId());
            if (!publicTestCases.isEmpty()) {
                stdin = publicTestCases.get(0).getInput();
                expectedOutput = publicTestCases.get(0).getExpectedOutput();
            } else {
                stdin = "";
            }
        }

        String token = judge0Client.createSubmissionFast(
                Judge0LanguageMapper.toJudge0LanguageId(language), sourceCode, stdin, expectedOutput);

        // "Run Code" is latency-sensitive from the user's perspective, so poll synchronously in a short bounded loop
        // rather than deferring to the async worker (which targets "Submit Solution").
        Judge0Submission result = null;
        for (int attempt = 0; attempt < 20; attempt++) {
            result = judge0Client.getSubmissionFast(token);
            if (result.getStatusId() != null && result.getStatusId() > 2) {
                break;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return result;
    }

    /**
     * "Submit Solution" — submits all test cases as a batch and creates a {@code PENDING} judge_submission row.
     * Finalization (polling Judge0 + computing the verdict + awarding points) is handled asynchronously by
     * {@code Judge0PollingService} (see scheduled/judge), matching the existing Job/JobStatus polling pattern.
     */
    public JudgeSubmission submitSolution(final User user, final Problem problem, final JudgeLanguage language, final String sourceCode) {
        return submitSolution(user, problem, language, sourceCode, Optional.empty());
    }

    public JudgeSubmission submitSolution(
            final User user,
            final Problem problem,
            final JudgeLanguage language,
            final String sourceCode,
            final Optional<String> lobbyId) {
        List<TestCase> testCases = testCaseRepository.getTestCasesByProblemId(problem.getId());
        if (testCases.isEmpty()) {
            throw new IllegalStateException("Problem " + problem.getId() + " has no test cases configured");
        }

        JudgeSubmission submission = JudgeSubmission.builder()
                .userId(user.getId())
                .problemId(problem.getId())
                .lobbyId(lobbyId)
                .language(language)
                .sourceCode(sourceCode)
                .status(SubmissionStatus.PENDING)
                .testCasesPassed(0)
                .testCasesTotal(testCases.size())
                .pointsAwarded(0)
                .build();
        submission = judgeSubmissionRepository.createSubmission(submission);

        List<String> stdins = testCases.stream().map(TestCase::getInput).toList();
        List<String> expectedOutputs = testCases.stream().map(TestCase::getExpectedOutput).toList();
        List<String> tokens = judge0Client.createSubmissionBatch(
                Judge0LanguageMapper.toJudge0LanguageId(language), sourceCode, stdins, expectedOutputs);

        for (int i = 0; i < testCases.size(); i++) {
            JudgeSubmissionResult result = JudgeSubmissionResult.builder()
                    .judgeSubmissionId(submission.getId())
                    .testCaseId(testCases.get(i).getId())
                    .judge0Token(Optional.of(tokens.get(i)))
                    .status(SubmissionStatus.PENDING)
                    .build();
            judgeSubmissionResultRepository.createResult(result);
        }

        return submission;
    }

    /**
     * Aggregates per-test-case statuses into a single verdict. First non-accepted category wins, prioritized
     * Compile Error > Internal Error > Runtime Error > TLE > MLE > Wrong Answer > Accepted, since a compile error
     * affects every test case identically and should surface over a "some tests failed" style verdict.
     */
    public SubmissionStatus computeVerdict(final List<JudgeSubmissionResult> results) {
        for (SubmissionStatus candidate : VERDICT_PRIORITY) {
            if (results.stream().anyMatch(r -> r.getStatus() == candidate)) {
                return candidate;
            }
        }
        return SubmissionStatus.ACCEPTED;
    }

    /**
     * Finalizes a judge_submission once all of its per-test-case results have resolved: computes the verdict,
     * persists aggregate stats, and — on first-ever ACCEPTED for this user+problem — awards points and updates the
     * leaderboard, mirroring {@code SubmissionsHandler.handleSubmissions}'s "already solved" guard.
     */
    public void finalizeSubmission(final JudgeSubmission submission, final Problem problem, final List<JudgeSubmissionResult> results) {
        SubmissionStatus verdict = computeVerdict(results);
        int passed = (int) results.stream().filter(r -> r.getStatus() == SubmissionStatus.ACCEPTED).count();
        Integer maxRuntime = results.stream()
                .map(r -> r.getRuntimeMs().orElse(null))
                .filter(java.util.Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(null);
        Integer maxMemory = results.stream()
                .map(r -> r.getMemoryKb().orElse(null))
                .filter(java.util.Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(null);

        int pointsAwarded = 0;
        if (verdict == SubmissionStatus.ACCEPTED) {
            boolean alreadySolved = judgeSubmissionRepository
                    .getAcceptedSubmissionByUserAndProblem(submission.getUserId(), problem.getId())
                    .isPresent();

            if (!alreadySolved) {
                pointsAwarded = JudgeScoreCalculator.calculateScore(problem.getDifficulty());

                Optional<Leaderboard> recentLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();
                if (recentLeaderboard.isPresent()) {
                    UserWithScore userWithScore = userRepository.getUserWithScoreByIdAndLeaderboardId(
                            submission.getUserId(), recentLeaderboard.get().getId(), UserFilterOptions.DEFAULT);
                    leaderboardRepository.updateUserPointsFromLeaderboard(
                            recentLeaderboard.get().getId(),
                            submission.getUserId(),
                            userWithScore.getTotalScore() + pointsAwarded);
                } else {
                    log.warn("No recent leaderboard found; awarding 0 points for submission {}", submission.getId());
                    pointsAwarded = 0;
                }
            }
        }

        judgeSubmissionRepository.updateSubmissionStatus(
                submission.getId(), verdict, passed, results.size(), maxRuntime, maxMemory, pointsAwarded);
    }

    public List<JudgeSubmissionResult> getResultsForSubmission(final String submissionId) {
        return new ArrayList<>(judgeSubmissionResultRepository.getResultsBySubmissionId(submissionId));
    }
}
