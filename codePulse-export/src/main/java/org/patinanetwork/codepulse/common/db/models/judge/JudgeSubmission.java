package org.patinanetwork.codepulse.common.db.models.judge;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.patinanetwork.codepulse.common.db.helper.annotations.JoinColumn;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
public class JudgeSubmission {

    private String id;

    private String userId;

    private String problemId;

    @Builder.Default
    private Optional<String> lobbyId = Optional.empty();

    private JudgeLanguage language;

    private String sourceCode;

    private SubmissionStatus status;

    private int testCasesPassed;

    private int testCasesTotal;

    @Builder.Default
    private Optional<Integer> maxRuntimeMs = Optional.empty();

    @Builder.Default
    private Optional<Integer> maxMemoryKb = Optional.empty();

    private int pointsAwarded;

    private OffsetDateTime createdAt;

    @Builder.Default
    private Optional<OffsetDateTime> completedAt = Optional.empty();

    @JoinColumn
    private List<JudgeSubmissionResult> results;
}
