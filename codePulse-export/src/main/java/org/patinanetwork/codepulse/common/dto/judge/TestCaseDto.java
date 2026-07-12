package org.patinanetwork.codepulse.common.dto.judge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codepulse.common.db.models.judge.TestCase;

/** Public-facing view of a test case; hidden test cases' input/output are never shown to non-admin callers. */
@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class TestCaseDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String input;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String expectedOutput;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int displayOrder;

    public static TestCaseDto fromTestCase(final TestCase testCase) {
        return TestCaseDto.builder()
                .id(testCase.getId())
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .displayOrder(testCase.getDisplayOrder())
                .build();
    }
}
