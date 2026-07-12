package org.patinanetwork.codepulse.common.judge;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmission;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmissionResult;
import org.patinanetwork.codepulse.common.db.models.judge.Problem;
import org.patinanetwork.codepulse.common.db.models.judge.SubmissionStatus;
import org.patinanetwork.codepulse.common.db.models.leaderboard.Leaderboard;
import org.patinanetwork.codepulse.common.db.models.question.QuestionDifficulty;
import org.patinanetwork.codepulse.common.db.models.user.UserWithScore;
import org.patinanetwork.codepulse.common.db.repos.judge.JudgeSubmissionRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.JudgeSubmissionResultRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.ProblemRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.TestCaseRepository;
import org.patinanetwork.codepulse.common.db.repos.leaderboard.LeaderboardRepository;
import org.patinanetwork.codepulse.common.db.repos.user.UserRepository;
import org.patinanetwork.codepulse.common.db.repos.user.options.UserFilterOptions;
import org.patinanetwork.codepulse.common.judge0.throttled.ThrottledJudge0Client;

public class Judge0ServiceTest {

    private final ProblemRepository problemRepository = mock(ProblemRepository.class);
    private final TestCaseRepository testCaseRepository = mock(TestCaseRepository.class);
    private final JudgeSubmissionRepository judgeSubmissionRepository = mock(JudgeSubmissionRepository.class);
    private final JudgeSubmissionResultRepository judgeSubmissionResultRepository =
            mock(JudgeSubmissionResultRepository.class);
    private final ThrottledJudge0Client judge0Client = mock(ThrottledJudge0Client.class);
    private final LeaderboardRepository leaderboardRepository = mock(LeaderboardRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    private final Judge0Service judge0Service = new Judge0Service(
            problemRepository,
            testCaseRepository,
            judgeSubmissionRepository,
            judgeSubmissionResultRepository,
            judge0Client,
            leaderboardRepository,
            userRepository);

    private JudgeSubmissionResult resultWith(final SubmissionStatus status) {
        return JudgeSubmissionResult.builder()
                .id("result-id")
                .judgeSubmissionId("submission-id")
                .testCaseId("test-case-id")
                .status(status)
                .build();
    }

    // --- computeVerdict priority ordering ---

    @Test
    public void computeVerdict_allAccepted_returnsAccepted() {
        List<JudgeSubmissionResult> results =
                List.of(resultWith(SubmissionStatus.ACCEPTED), resultWith(SubmissionStatus.ACCEPTED));

        assertEquals(SubmissionStatus.ACCEPTED, judge0Service.computeVerdict(results));
    }

    @Test
    public void computeVerdict_compileErrorBeatsEverythingElse() {
        List<JudgeSubmissionResult> results = List.of(
                resultWith(SubmissionStatus.ACCEPTED),
                resultWith(SubmissionStatus.WRONG_ANSWER),
                resultWith(SubmissionStatus.COMPILE_ERROR),
                resultWith(SubmissionStatus.TIME_LIMIT_EXCEEDED));

        assertEquals(SubmissionStatus.COMPILE_ERROR, judge0Service.computeVerdict(results));
    }

    @Test
    public void computeVerdict_runtimeErrorBeatsTleMleWrongAnswer() {
        List<JudgeSubmissionResult> results = List.of(
                resultWith(SubmissionStatus.WRONG_ANSWER),
                resultWith(SubmissionStatus.TIME_LIMIT_EXCEEDED),
                resultWith(SubmissionStatus.MEMORY_LIMIT_EXCEEDED),
                resultWith(SubmissionStatus.RUNTIME_ERROR));

        assertEquals(SubmissionStatus.RUNTIME_ERROR, judge0Service.computeVerdict(results));
    }

    @Test
    public void computeVerdict_tleBeatsMleAndWrongAnswer() {
        List<JudgeSubmissionResult> results = List.of(
                resultWith(SubmissionStatus.WRONG_ANSWER),
                resultWith(SubmissionStatus.MEMORY_LIMIT_EXCEEDED),
                resultWith(SubmissionStatus.TIME_LIMIT_EXCEEDED));

        assertEquals(SubmissionStatus.TIME_LIMIT_EXCEEDED, judge0Service.computeVerdict(results));
    }

    @Test
    public void computeVerdict_mleBeatsWrongAnswer() {
        List<JudgeSubmissionResult> results =
                List.of(resultWith(SubmissionStatus.WRONG_ANSWER), resultWith(SubmissionStatus.MEMORY_LIMIT_EXCEEDED));

        assertEquals(SubmissionStatus.MEMORY_LIMIT_EXCEEDED, judge0Service.computeVerdict(results));
    }

    @Test
    public void computeVerdict_someWrongAnswer_returnsWrongAnswer() {
        List<JudgeSubmissionResult> results =
                List.of(resultWith(SubmissionStatus.ACCEPTED), resultWith(SubmissionStatus.WRONG_ANSWER));

        assertEquals(SubmissionStatus.WRONG_ANSWER, judge0Service.computeVerdict(results));
    }

    // --- finalizeSubmission: scoring + leaderboard + already-solved guard ---

    private Problem problemWithDifficulty(final QuestionDifficulty difficulty) {
        return Problem.builder()
                .id("problem-id")
                .title("Two Sum")
                .slug("two-sum")
                .difficulty(difficulty)
                .statement("statement")
                .constraints("constraints")
                .timeLimitMs(1000)
                .memoryLimitKb(65536)
                .createdBy("admin-id")
                .build();
    }

    private JudgeSubmission pendingSubmission() {
        return JudgeSubmission.builder()
                .id("submission-id")
                .userId("user-id")
                .problemId("problem-id")
                .language(org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage.PYTHON3)
                .sourceCode("print('hi')")
                .status(SubmissionStatus.PENDING)
                .testCasesPassed(0)
                .testCasesTotal(1)
                .pointsAwarded(0)
                .build();
    }

    @Test
    public void finalizeSubmission_firstAcceptedSolve_awardsPointsAndUpdatesLeaderboard() {
        JudgeSubmission submission = pendingSubmission();
        Problem problem = problemWithDifficulty(QuestionDifficulty.Easy);
        List<JudgeSubmissionResult> results = List.of(resultWith(SubmissionStatus.ACCEPTED));

        when(judgeSubmissionRepository.getAcceptedSubmissionByUserAndProblem("user-id", "problem-id"))
                .thenReturn(Optional.empty());

        Leaderboard leaderboard = Leaderboard.builder().id("leaderboard-id").build();
        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(Optional.of(leaderboard));

        UserWithScore userWithScore =
                UserWithScore.builder().id("user-id").totalScore(50).build();
        when(userRepository.getUserWithScoreByIdAndLeaderboardId("user-id", "leaderboard-id", UserFilterOptions.DEFAULT))
                .thenReturn(userWithScore);

        judge0Service.finalizeSubmission(submission, problem, results);

        verify(leaderboardRepository)
                .updateUserPointsFromLeaderboard(eq("leaderboard-id"), eq("user-id"), intThat(newTotal -> newTotal > 50));
        verify(judgeSubmissionRepository)
                .updateSubmissionStatus(
                        eq("submission-id"),
                        eq(SubmissionStatus.ACCEPTED),
                        eq(1),
                        eq(1),
                        any(),
                        any(),
                        intThat(points -> points > 0));
    }

    @Test
    public void finalizeSubmission_alreadySolved_awardsZeroPointsAndDoesNotTouchLeaderboard() {
        JudgeSubmission submission = pendingSubmission();
        Problem problem = problemWithDifficulty(QuestionDifficulty.Easy);
        List<JudgeSubmissionResult> results = List.of(resultWith(SubmissionStatus.ACCEPTED));

        when(judgeSubmissionRepository.getAcceptedSubmissionByUserAndProblem("user-id", "problem-id"))
                .thenReturn(Optional.of(pendingSubmission()));

        judge0Service.finalizeSubmission(submission, problem, results);

        verify(leaderboardRepository, never()).getRecentLeaderboardMetadata();
        verify(leaderboardRepository, never()).updateUserPointsFromLeaderboard(anyString(), anyString(), anyInt());
        verify(judgeSubmissionRepository)
                .updateSubmissionStatus(eq("submission-id"), eq(SubmissionStatus.ACCEPTED), eq(1), eq(1), any(), any(), eq(0));
    }

    @Test
    public void finalizeSubmission_wrongAnswer_awardsNoPointsRegardlessOfSolveHistory() {
        JudgeSubmission submission = pendingSubmission();
        Problem problem = problemWithDifficulty(QuestionDifficulty.Easy);
        List<JudgeSubmissionResult> results =
                List.of(resultWith(SubmissionStatus.ACCEPTED), resultWith(SubmissionStatus.WRONG_ANSWER));

        judge0Service.finalizeSubmission(submission, problem, results);

        verify(judgeSubmissionRepository, never())
                .getAcceptedSubmissionByUserAndProblem(anyString(), anyString());
        verify(leaderboardRepository, never()).updateUserPointsFromLeaderboard(anyString(), anyString(), anyInt());
        verify(judgeSubmissionRepository)
                .updateSubmissionStatus(eq("submission-id"), eq(SubmissionStatus.WRONG_ANSWER), eq(1), eq(2), any(), any(), eq(0));
    }

    @Test
    public void finalizeSubmission_noRecentLeaderboard_awardsZeroPointsInsteadOfThrowing() {
        JudgeSubmission submission = pendingSubmission();
        Problem problem = problemWithDifficulty(QuestionDifficulty.Hard);
        List<JudgeSubmissionResult> results = List.of(resultWith(SubmissionStatus.ACCEPTED));

        when(judgeSubmissionRepository.getAcceptedSubmissionByUserAndProblem("user-id", "problem-id"))
                .thenReturn(Optional.empty());
        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(Optional.empty());

        judge0Service.finalizeSubmission(submission, problem, results);

        verify(leaderboardRepository, never()).updateUserPointsFromLeaderboard(anyString(), anyString(), anyInt());
        verify(judgeSubmissionRepository)
                .updateSubmissionStatus(eq("submission-id"), eq(SubmissionStatus.ACCEPTED), eq(1), eq(1), any(), any(), eq(0));
    }

    // --- judge0StatusToSubmissionStatus mapping ---

    @Test
    public void judge0StatusToSubmissionStatus_mapsKnownStatusIds() {
        assertEquals(SubmissionStatus.RUNNING, Judge0Service.judge0StatusToSubmissionStatus(1));
        assertEquals(SubmissionStatus.RUNNING, Judge0Service.judge0StatusToSubmissionStatus(2));
        assertEquals(SubmissionStatus.ACCEPTED, Judge0Service.judge0StatusToSubmissionStatus(3));
        assertEquals(SubmissionStatus.WRONG_ANSWER, Judge0Service.judge0StatusToSubmissionStatus(4));
        assertEquals(SubmissionStatus.TIME_LIMIT_EXCEEDED, Judge0Service.judge0StatusToSubmissionStatus(5));
        assertEquals(SubmissionStatus.COMPILE_ERROR, Judge0Service.judge0StatusToSubmissionStatus(6));
        assertEquals(SubmissionStatus.RUNTIME_ERROR, Judge0Service.judge0StatusToSubmissionStatus(9));
        assertEquals(SubmissionStatus.INTERNAL_ERROR, Judge0Service.judge0StatusToSubmissionStatus(13));
        assertEquals(SubmissionStatus.INTERNAL_ERROR, Judge0Service.judge0StatusToSubmissionStatus(999));
    }
}
