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
public class TestCase {

    private String id;

    private String problemId;

    private String input;

    private String expectedOutput;

    private boolean isHidden;

    private int displayOrder;
}
