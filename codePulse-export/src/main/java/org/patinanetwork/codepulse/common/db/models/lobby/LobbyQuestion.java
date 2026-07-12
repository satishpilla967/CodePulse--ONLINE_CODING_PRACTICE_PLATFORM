package org.patinanetwork.codepulse.common.db.models.lobby;

import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class LobbyQuestion {

    private String id;

    private String lobbyId;

    /** @deprecated LeetCode-sourced questions are no longer assigned to new duels; use {@link #problemId} instead. */
    @Deprecated
    @Builder.Default
    private Optional<String> questionBankId = Optional.empty();

    /** The internally-authored judge problem assigned to this duel round. */
    @Builder.Default
    private Optional<String> problemId = Optional.empty();

    private OffsetDateTime createdAt;

    private int userSolvedCount;
}
