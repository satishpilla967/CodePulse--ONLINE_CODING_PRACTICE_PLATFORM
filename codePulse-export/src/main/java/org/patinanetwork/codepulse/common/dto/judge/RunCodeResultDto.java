package org.patinanetwork.codepulse.common.dto.judge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codepulse.common.db.models.judge.SubmissionStatus;
import org.patinanetwork.codepulse.common.judge0.Judge0Submission;

/** Raw result of a "Run Code" execution — never persisted, never awards points. */
@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class RunCodeResultDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private SubmissionStatus status;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String stdout;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String stderr;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String compileOutput;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Double timeSeconds;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Long memoryKb;

    public static RunCodeResultDto fromJudge0Submission(final Judge0Submission submission) {
        return RunCodeResultDto.builder()
                .status(
                        submission.getStatusId() != null
                                ? org.patinanetwork.codepulse.common.judge.Judge0Service.judge0StatusToSubmissionStatus(
                                        submission.getStatusId())
                                : SubmissionStatus.INTERNAL_ERROR)
                .stdout(submission.getStdout())
                .stderr(submission.getStderr())
                .compileOutput(submission.getCompileOutput())
                .timeSeconds(submission.getTime())
                .memoryKb(submission.getMemory())
                .build();
    }
}
