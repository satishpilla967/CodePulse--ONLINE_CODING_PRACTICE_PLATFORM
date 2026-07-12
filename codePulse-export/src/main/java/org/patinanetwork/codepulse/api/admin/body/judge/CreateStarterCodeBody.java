package org.patinanetwork.codepulse.api.admin.body.judge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage;

@Getter
@Setter
public class CreateStarterCodeBody {

    @NotBlank
    private String problemId;

    @NotNull
    private JudgeLanguage language;

    @NotBlank
    private String starterCode;
}
