package org.patinanetwork.codepulse.common.dto.judge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codepulse.common.db.models.judge.TestCase;

/** Admin-only view of a test case; unlike {@link TestCaseDto}, this includes hidden test cases and the isHidden flag. */
@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class TestCaseAdminDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String problemId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String input;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String expectedOutput;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isHidden;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int displayOrder;

    public static TestCaseAdminDto fromTestCase(final TestCase testCase) {
        return TestCaseAdminDto.builder()
                .id(testCase.getId())
                .problemId(testCase.getProblemId())
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .isHidden(testCase.isHidden())
                .displayOrder(testCase.getDisplayOrder())
                .build();
    }
}
