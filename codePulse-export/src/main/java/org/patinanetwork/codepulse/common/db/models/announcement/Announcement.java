package org.patinanetwork.codepulse.common.db.models.announcement;

import java.time.OffsetDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.patinanetwork.codepulse.common.db.helper.annotations.NotNullColumn;
import org.patinanetwork.codepulse.common.db.helper.annotations.NullColumn;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Announcement {

    @NotNullColumn
    private String id;

    @NotNullColumn
    private OffsetDateTime createdAt;

    @NotNullColumn
    private OffsetDateTime expiresAt;

    @NullColumn
    private boolean showTimer;

    @NotNullColumn
    private String message;
}
