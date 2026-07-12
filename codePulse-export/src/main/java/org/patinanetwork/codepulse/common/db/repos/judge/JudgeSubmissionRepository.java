package org.patinanetwork.codepulse.common.db.repos.judge;

import java.util.List;
import java.util.Optional;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmission;
import org.patinanetwork.codepulse.common.db.models.judge.SubmissionStatus;

public interface JudgeSubmissionRepository {

    JudgeSubmission createSubmission(JudgeSubmission submission);

    JudgeSubmission getSubmissionById(String id);

    Optional<JudgeSubmission> getAcceptedSubmissionByUserAndProblem(String userId, String problemId);

    List<JudgeSubmission> getSubmissionsByStatuses(List<SubmissionStatus> statuses);

    List<JudgeSubmission> getSubmissionsByUserAndLobbyAndProblem(String userId, String lobbyId, String problemId);

    void updateSubmissionStatus(
            String id,
            SubmissionStatus status,
            int testCasesPassed,
            int testCasesTotal,
            Integer maxRuntimeMs,
            Integer maxMemoryKb,
            int pointsAwarded);
}
