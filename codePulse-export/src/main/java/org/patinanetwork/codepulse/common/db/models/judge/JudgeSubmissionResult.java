package org.patinanetwork.codepulse.common.db.models.judge;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
public class JudgeSubmissionResult {

    private String id;

    private String judgeSubmissionId;

    private String testCaseId;

    @Builder.Default
    private Optional<String> judge0Token = Optional.empty();

    private SubmissionStatus status;

    @Builder.Default
    private Optional<String> stdout = Optional.empty();

    @Builder.Default
    private Optional<String> stderr = Optional.empty();

    @Builder.Default
    private Optional<Integer> runtimeMs = Optional.empty();

    @Builder.Default
    private Optional<Integer> memoryKb = Optional.empty();
}
