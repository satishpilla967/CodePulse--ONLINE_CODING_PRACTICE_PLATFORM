package org.patinanetwork.codepulse.common.dto.judge;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmission;
import org.patinanetwork.codepulse.common.db.models.judge.SubmissionStatus;

@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class JudgeSubmissionDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String problemId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private JudgeLanguage language;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private SubmissionStatus status;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int testCasesPassed;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int testCasesTotal;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Integer maxRuntimeMs;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Integer maxMemoryKb;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int pointsAwarded;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private OffsetDateTime completedAt;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private List<JudgeSubmissionResultDto> results;

    public static JudgeSubmissionDto fromJudgeSubmission(final JudgeSubmission submission) {
        return JudgeSubmissionDto.builder()
                .id(submission.getId())
                .problemId(submission.getProblemId())
                .language(submission.getLanguage())
                .status(submission.getStatus())
                .testCasesPassed(submission.getTestCasesPassed())
                .testCasesTotal(submission.getTestCasesTotal())
                .maxRuntimeMs(submission.getMaxRuntimeMs().orElse(null))
                .maxMemoryKb(submission.getMaxMemoryKb().orElse(null))
                .pointsAwarded(submission.getPointsAwarded())
                .createdAt(submission.getCreatedAt())
                .completedAt(submission.getCompletedAt().orElse(null))
                .results(
                        submission.getResults() != null
                                ? submission.getResults().stream()
                                        .map(JudgeSubmissionResultDto::fromJudgeSubmissionResult)
                                        .toList()
                                : List.of())
                .build();
    }
}
