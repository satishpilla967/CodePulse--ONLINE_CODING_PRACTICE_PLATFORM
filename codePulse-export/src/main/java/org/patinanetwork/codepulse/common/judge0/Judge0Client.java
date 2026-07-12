package org.patinanetwork.codepulse.common.judge0;

import java.util.List;

/** Client for a self-hosted Judge0 CE instance. */
public interface Judge0Client {

    /** Creates a single submission and returns the Judge0 token to poll for its result. */
    String createSubmission(int languageId, String sourceCode, String stdin, String expectedOutput);

    /** Creates a batch of submissions (one per test case) and returns their Judge0 tokens, in order. */
    List<String> createSubmissionBatch(int languageId, String sourceCode, List<String> stdins, List<String> expectedOutputs);

    /** Fetches the current state of a submission by token. */
    Judge0Submission getSubmission(String token);

    /** Fetches the current state of a batch of submissions by token. */
    List<Judge0Submission> getSubmissionBatch(List<String> tokens);
}
