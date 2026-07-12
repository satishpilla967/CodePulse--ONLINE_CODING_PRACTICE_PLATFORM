package org.patinanetwork.codepulse.common.db.models.judge;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.patinanetwork.codepulse.common.db.helper.annotations.JoinColumn;
import org.patinanetwork.codepulse.common.db.models.question.QuestionDifficulty;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
public class Problem {

    private String id;

    private String title;

    private String slug;

    private QuestionDifficulty difficulty;

    private ProblemCategory category;

    private String statement;

    private String constraints;

    private int timeLimitMs;

    private int memoryLimitKb;

    private String createdBy;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @JoinColumn
    private List<ProblemStarterCode> starterCode;

    @JoinColumn
    private List<TestCase> testCases;
}
