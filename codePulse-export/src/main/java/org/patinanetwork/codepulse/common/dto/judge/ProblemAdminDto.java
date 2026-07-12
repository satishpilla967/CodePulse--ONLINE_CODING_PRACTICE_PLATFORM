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
import org.patinanetwork.codepulse.common.db.models.judge.ProblemBuggyCode;
import org.patinanetwork.codepulse.common.db.models.judge.ProblemCategory;
import org.patinanetwork.codepulse.common.db.models.judge.ProblemStarterCode;
import org.patinanetwork.codepulse.common.db.models.judge.TestCase;
import org.patinanetwork.codepulse.common.db.models.question.QuestionDifficulty;

/**
 * Admin-only view of a problem, including hidden test cases and all per-language starter code. Never expose this DTO
 * to non-admin callers, since it leaks hidden test case input/output.
 */
@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class ProblemAdminDto {

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
    private List<TestCaseAdminDto> testCases;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private List<ProblemStarterCodeDto> starterCode;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private List<ProblemBuggyCodeDto> buggyCode;

    public static ProblemAdminDto fromProblem(
            final Problem problem,
            final List<TestCase> testCases,
            final List<ProblemStarterCode> starterCode,
            final List<ProblemBuggyCode> buggyCode) {
        return ProblemAdminDto.builder()
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
                .testCases(
                        testCases.stream().map(TestCaseAdminDto::fromTestCase).toList())
                .starterCode(starterCode.stream()
                        .map(ProblemStarterCodeDto::fromProblemStarterCode)
                        .toList())
                .buggyCode(buggyCode.stream()
                        .map(ProblemBuggyCodeDto::fromProblemBuggyCode)
                        .toList())
                .build();
    }
}
