package org.patinanetwork.codepulse.scheduled.judge;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmission;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmissionResult;
import org.patinanetwork.codepulse.common.db.models.judge.Problem;
import org.patinanetwork.codepulse.common.db.models.judge.SubmissionStatus;
import org.patinanetwork.codepulse.common.db.repos.judge.JudgeSubmissionRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.JudgeSubmissionResultRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.ProblemRepository;
import org.patinanetwork.codepulse.common.dto.Empty;
import org.patinanetwork.codepulse.common.judge.Judge0Service;
import org.patinanetwork.codepulse.common.judge0.Judge0Submission;
import org.patinanetwork.codepulse.common.judge0.throttled.ThrottledJudge0Client;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Async drain-queue worker for `judge_submission` rows, directly modeled on
 * {@code LeetcodeQuestionProcessService.drainQueue}: picks up PENDING/RUNNING submissions, polls Judge0 for each
 * unresolved per-test-case token, persists results, and finalizes (verdict + points + leaderboard) once every token
 * for a submission has resolved.
 */
@Slf4j
@Component
@Profile("!ci")
public class Judge0PollingService {

    private static final ReentrantLock LOCK = new ReentrantLock();

    private final JudgeSubmissionRepository judgeSubmissionRepository;
    private final JudgeSubmissionResultRepository judgeSubmissionResultRepository;
    private final ProblemRepository problemRepository;
    private final ThrottledJudge0Client judge0Client;
    private final Judge0Service judge0Service;

    public Judge0PollingService(
            final JudgeSubmissionRepository judgeSubmissionRepository,
            final JudgeSubmissionResultRepository judgeSubmissionResultRepository,
            final ProblemRepository problemRepository,
            final ThrottledJudge0Client judge0Client,
            final Judge0Service judge0Service) {
        this.judgeSubmissionRepository = judgeSubmissionRepository;
        this.judgeSubmissionResultRepository = judgeSubmissionResultRepository;
        this.problemRepository = problemRepository;
        this.judge0Client = judge0Client;
        this.judge0Service = judge0Service;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    @Async
    public CompletableFuture<Empty> drainQueue() {
        if (!LOCK.tryLock()) {
            log.info("thread attempted to drain judge0 queue, but queue is already being drained.");
            return CompletableFuture.completedFuture(Empty.of());
        }

        try {
            List<JudgeSubmission> pending = judgeSubmissionRepository.getSubmissionsByStatuses(
                    List.of(SubmissionStatus.PENDING, SubmissionStatus.RUNNING));

            if (pending.isEmpty()) {
                return CompletableFuture.completedFuture(Empty.of());
            }

            log.info("Found {} judge submissions to poll", pending.size());

            for (JudgeSubmission submission : pending) {
                try {
                    pollAndMaybeFinalize(submission);
                } catch (Exception e) {
                    log.error("Failed to poll judge submission {}", submission.getId(), e);
                }
            }
        } finally {
            LOCK.unlock();
        }
        return CompletableFuture.completedFuture(Empty.of());
    }

    private void pollAndMaybeFinalize(final JudgeSubmission submission) {
        List<JudgeSubmissionResult> results = judgeSubmissionResultRepository.getResultsBySubmissionId(submission.getId());

        List<String> tokens = results.stream()
                .filter(r -> r.getStatus() == SubmissionStatus.PENDING || r.getStatus() == SubmissionStatus.RUNNING)
                .map(r -> r.getJudge0Token().orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();

        if (!tokens.isEmpty()) {
            List<Judge0Submission> judge0Results = judge0Client.getSubmissionBatchFast(tokens);
            for (int i = 0; i < tokens.size(); i++) {
                Judge0Submission j0 = judge0Results.size() > i ? judge0Results.get(i) : null;
                if (j0 == null || j0.getStatusId() == null) {
                    continue;
                }
                SubmissionStatus status = Judge0Service.judge0StatusToSubmissionStatus(j0.getStatusId());
                if (status == SubmissionStatus.RUNNING) {
                    continue;
                }
                String token = tokens.get(i);
                JudgeSubmissionResult matching = results.stream()
                        .filter(r -> token.equals(r.getJudge0Token().orElse(null)))
                        .findFirst()
                        .orElse(null);
                if (matching != null) {
                    judgeSubmissionResultRepository.updateResult(
                            matching.getId(),
                            status,
                            j0.getStdout(),
                            j0.getStderr() != null ? j0.getStderr() : j0.getCompileOutput(),
                            j0.getTime() != null ? (int) Math.round(j0.getTime() * 1000) : null,
                            j0.getMemory() != null ? j0.getMemory().intValue() : null);
                }
            }
        }

        List<JudgeSubmissionResult> refreshed = judgeSubmissionResultRepository.getResultsBySubmissionId(submission.getId());
        boolean allResolved = refreshed.stream()
                .noneMatch(r -> r.getStatus() == SubmissionStatus.PENDING || r.getStatus() == SubmissionStatus.RUNNING);

        if (!allResolved) {
            if (submission.getStatus() == SubmissionStatus.PENDING) {
                judgeSubmissionRepository.updateSubmissionStatus(
                        submission.getId(), SubmissionStatus.RUNNING, 0, submission.getTestCasesTotal(), null, null, 0);
            }
            return;
        }

        Problem problem = problemRepository.getProblemById(submission.getProblemId());
        judge0Service.finalizeSubmission(submission, problem, refreshed);
    }
}
