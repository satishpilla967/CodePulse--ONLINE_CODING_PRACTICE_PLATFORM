package org.patinanetwork.codepulse.common.dto.judge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage;
import org.patinanetwork.codepulse.common.db.models.judge.ProblemStarterCode;

@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class ProblemStarterCodeDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String problemId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private JudgeLanguage language;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String starterCode;

    public static ProblemStarterCodeDto fromProblemStarterCode(final ProblemStarterCode starterCode) {
        return ProblemStarterCodeDto.builder()
                .problemId(starterCode.getProblemId())
                .language(starterCode.getLanguage())
                .starterCode(starterCode.getStarterCode())
                .build();
    }
}
