package org.patinanetwork.codepulse.common.judge0.throttled;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BlockingBucket;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.patinanetwork.codepulse.common.judge0.Judge0Client;
import org.patinanetwork.codepulse.common.judge0.Judge0ClientImpl;
import org.patinanetwork.codepulse.common.judge0.Judge0Submission;
import org.patinanetwork.codepulse.common.utils.lock.QueueLock;
import org.springframework.stereotype.Component;

@Component
public class ThrottledJudge0ClientImpl implements ThrottledJudge0Client {
    private final Judge0Client judge0Client;

    private static final long REQUESTS_OVER_TIME = 5L;
    private static final long MILLISECONDS_TO_WAIT = 200L;
    private final QueueLock rateLimiter;

    private BlockingBucket initializeBucket() {
        var bandwidth = Bandwidth.builder()
                .capacity(REQUESTS_OVER_TIME)
                .refillIntervally(REQUESTS_OVER_TIME, Duration.ofMillis(MILLISECONDS_TO_WAIT))
                .build();

        return Bucket.builder().addLimit(bandwidth).build().asBlocking();
    }

    private void waitForToken(boolean fast) {
        try {
            if (fast) {
                rateLimiter.acquireFast();
            } else {
                rateLimiter.acquire();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to consume rate limit bucket token in judge0 client", e);
        }
    }

    public ThrottledJudge0ClientImpl(final Judge0ClientImpl judge0ClientImpl, final ExecutorService virtualPool) {
        this.rateLimiter = new QueueLock(initializeBucket(), virtualPool);
        this.judge0Client = judge0ClientImpl;
    }

    @Override
    public String createSubmissionFast(
            final int languageId, final String sourceCode, final String stdin, final String expectedOutput) {
        waitForToken(true);
        return judge0Client.createSubmission(languageId, sourceCode, stdin, expectedOutput);
    }

    @Override
    public List<String> createSubmissionBatchFast(
            final int languageId, final String sourceCode, final List<String> stdins, final List<String> expectedOutputs) {
        waitForToken(true);
        return judge0Client.createSubmissionBatch(languageId, sourceCode, stdins, expectedOutputs);
    }

    @Override
    public Judge0Submission getSubmissionFast(final String token) {
        waitForToken(true);
        return judge0Client.getSubmission(token);
    }

    @Override
    public List<Judge0Submission> getSubmissionBatchFast(final List<String> tokens) {
        waitForToken(true);
        return judge0Client.getSubmissionBatch(tokens);
    }

    @Override
    public String createSubmission(final int languageId, final String sourceCode, final String stdin, final String expectedOutput) {
        waitForToken(false);
        return judge0Client.createSubmission(languageId, sourceCode, stdin, expectedOutput);
    }

    @Override
    public List<String> createSubmissionBatch(
            final int languageId, final String sourceCode, final List<String> stdins, final List<String> expectedOutputs) {
        waitForToken(false);
        return judge0Client.createSubmissionBatch(languageId, sourceCode, stdins, expectedOutputs);
    }

    @Override
    public Judge0Submission getSubmission(final String token) {
        waitForToken(false);
        return judge0Client.getSubmission(token);
    }

    @Override
    public List<Judge0Submission> getSubmissionBatch(final List<String> tokens) {
        waitForToken(false);
        return judge0Client.getSubmissionBatch(tokens);
    }
}
