package org.patinanetwork.codepulse.common.dto.judge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmissionResult;
import org.patinanetwork.codepulse.common.db.models.judge.SubmissionStatus;

@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class JudgeSubmissionResultDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String testCaseId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private SubmissionStatus status;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String stdout;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String stderr;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Integer runtimeMs;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Integer memoryKb;

    public static JudgeSubmissionResultDto fromJudgeSubmissionResult(final JudgeSubmissionResult result) {
        return JudgeSubmissionResultDto.builder()
                .testCaseId(result.getTestCaseId())
                .status(result.getStatus())
                .stdout(result.getStdout().orElse(null))
                .stderr(result.getStderr().orElse(null))
                .runtimeMs(result.getRuntimeMs().orElse(null))
                .memoryKb(result.getMemoryKb().orElse(null))
                .build();
    }
}
