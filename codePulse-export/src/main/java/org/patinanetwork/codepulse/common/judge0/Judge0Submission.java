package org.patinanetwork.codepulse.common.judge0;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Wire-level representation of a single Judge0 CE submission request/response. */
@Getter
@Setter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Judge0Submission {

    private String token;

    private int languageId;

    private String sourceCode;

    private String stdin;

    private String expectedOutput;

    private String stdout;

    private String stderr;

    private String compileOutput;

    private Integer statusId;

    private String statusDescription;

    private Double time;

    private Long memory;
}
