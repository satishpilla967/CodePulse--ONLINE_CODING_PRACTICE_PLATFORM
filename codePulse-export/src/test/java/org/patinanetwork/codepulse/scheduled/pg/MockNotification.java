package org.patinanetwork.codepulse.scheduled.pg;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Test double for a Redis pub/sub notification, replacing the previous Postgres {@code
 * PGNotification}-backed mock now that {@link NotifyListener} subscribes via {@code JedisPubSub}.
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class MockNotification {

    private String name;
    private String parameter;
}
