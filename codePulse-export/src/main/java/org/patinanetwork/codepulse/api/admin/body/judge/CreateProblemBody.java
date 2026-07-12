package org.patinanetwork.codepulse.api.admin.body.judge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.patinanetwork.codepulse.common.db.models.judge.ProblemCategory;
import org.patinanetwork.codepulse.common.db.models.question.QuestionDifficulty;

@Getter
@Setter
public class CreateProblemBody {

    @NotBlank
    private String title;

    @NotBlank
    private String slug;

    @NotNull
    private QuestionDifficulty difficulty;

    /** Defaults to {@link ProblemCategory#DSA} when omitted. */
    private ProblemCategory category;

    @NotBlank
    private String statement;

    private String constraints;

    private Integer timeLimitMs;

    private Integer memoryLimitKb;
}
