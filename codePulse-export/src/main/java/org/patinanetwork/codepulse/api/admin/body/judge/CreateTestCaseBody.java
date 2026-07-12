package org.patinanetwork.codepulse.api.admin.body.judge;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTestCaseBody {

    @NotBlank
    private String problemId;

    @NotBlank
    private String input;

    @NotBlank
    private String expectedOutput;

    private boolean isHidden = true;

    private int displayOrder = 0;
}
