package org.patinanetwork.codepulse.common.dto.judge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage;
import org.patinanetwork.codepulse.common.db.models.judge.ProblemBuggyCode;

@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class ProblemBuggyCodeDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String problemId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private JudgeLanguage language;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String buggyCode;

    public static ProblemBuggyCodeDto fromProblemBuggyCode(final ProblemBuggyCode buggyCode) {
        return ProblemBuggyCodeDto.builder()
                .problemId(buggyCode.getProblemId())
                .language(buggyCode.getLanguage())
                .buggyCode(buggyCode.getBuggyCode())
                .build();
    }
}
