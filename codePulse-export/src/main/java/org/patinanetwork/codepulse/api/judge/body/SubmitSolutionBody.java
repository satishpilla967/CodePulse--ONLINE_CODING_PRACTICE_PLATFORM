package org.patinanetwork.codepulse.api.judge.body;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage;

@Getter
@Setter
public class SubmitSolutionBody {

    @NotBlank
    private String problemId;

    @NotNull
    private JudgeLanguage language;

    @NotBlank
    private String sourceCode;

    /** Optional; set when submitting as part of an active duel lobby. */
    private String lobbyId;
}
