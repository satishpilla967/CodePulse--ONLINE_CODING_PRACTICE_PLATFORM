package org.patinanetwork.codepulse.common.db.models.judge;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
public class ProblemStarterCode {

    private String id;

    private String problemId;

    private JudgeLanguage language;

    private String starterCode;
}
