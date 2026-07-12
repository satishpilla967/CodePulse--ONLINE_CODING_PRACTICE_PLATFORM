package org.patinanetwork.codepulse.common.dto.judge;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codepulse.common.db.models.judge.Problem;
import org.patinanetwork.codepulse.common.db.models.judge.ProblemCategory;
import org.patinanetwork.codepulse.common.db.models.question.QuestionDifficulty;

@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class ProblemDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String slug;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private QuestionDifficulty difficulty;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private ProblemCategory category;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String statement;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String constraints;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int timeLimitMs;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int memoryLimitKb;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private List<TestCaseDto> publicTestCases;

    public static ProblemDto fromProblem(final Problem problem) {
        return ProblemDto.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .slug(problem.getSlug())
                .difficulty(problem.getDifficulty())
                .category(problem.getCategory())
                .statement(problem.getStatement())
                .constraints(problem.getConstraints())
                .timeLimitMs(problem.getTimeLimitMs())
                .memoryLimitKb(problem.getMemoryLimitKb())
                .createdAt(problem.getCreatedAt())
                .publicTestCases(
                        problem.getTestCases() != null
                                ? problem.getTestCases().stream()
                                        .filter(tc -> !tc.isHidden())
                                        .map(TestCaseDto::fromTestCase)
                                        .toList()
                                : List.of())
                .build();
    }
}
