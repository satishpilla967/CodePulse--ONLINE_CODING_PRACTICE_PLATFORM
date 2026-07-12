package org.patinanetwork.codepulse.common.judge0.throttled;

import java.util.List;
import org.patinanetwork.codepulse.common.judge0.Judge0Client;
import org.patinanetwork.codepulse.common.judge0.Judge0Submission;

/**
 * Attaches a rate limiter over {@link Judge0Client} to avoid overwhelming the self-hosted Judge0 workers.
 *
 * <p>Methods that end in {@code Fast} skip the rate-limiter. <b>You should ONLY use this if the latency will directly
 * affect the user and/or any other latency-sensitive concerns.</b>
 */
public interface ThrottledJudge0Client extends Judge0Client {

    String createSubmissionFast(int languageId, String sourceCode, String stdin, String expectedOutput);

    List<String> createSubmissionBatchFast(
            int languageId, String sourceCode, List<String> stdins, List<String> expectedOutputs);

    Judge0Submission getSubmissionFast(String token);

    List<Judge0Submission> getSubmissionBatchFast(List<String> tokens);
}
