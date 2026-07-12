package org.patinanetwork.codepulse.common.db.repos.judge;

import java.util.List;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmissionResult;
import org.patinanetwork.codepulse.common.db.models.judge.SubmissionStatus;

public interface JudgeSubmissionResultRepository {

    JudgeSubmissionResult createResult(JudgeSubmissionResult result);

    List<JudgeSubmissionResult> getResultsBySubmissionId(String submissionId);

    void updateResult(String id, SubmissionStatus status, String stdout, String stderr, Integer runtimeMs, Integer memoryKb);
}
