package org.patinanetwork.codepulse.scheduled.pg;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codepulse.common.env.Env;
import org.patinanetwork.codepulse.common.redis.JedisClientManager;
import org.patinanetwork.codepulse.common.reporter.Reporter;
import org.patinanetwork.codepulse.common.reporter.report.Report;
import org.patinanetwork.codepulse.common.reporter.report.location.Location;
import org.patinanetwork.codepulse.scheduled.pg.handler.LobbyNotifyHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.UnifiedJedis;

/**
 * Listens for job/lobby change notifications. Originally implemented via Postgres LISTEN/NOTIFY +
 * triggers (see archived migrations {@code V0054__Create_notify_trigger_on_job_table.SQL} and
 * {@code V0057__Create_notify_trigger_on_lobby_table.SQL} under {@code db/migration/archive-postgres}).
 * Now that the database is MySQL (which has no equivalent), this is backed by Redis pub/sub: the
 * repository layer ({@code JobSqlRepository}, {@code LobbySqlRepository}) explicitly publishes to the
 * same channel names on write, and this class subscribes via {@link JedisPubSub}.
 *
 * <p>The public shape (channel names via {@link PgChannel}, dispatch to {@link LobbyNotifyHandler}) is preserved so
 * downstream consumers don't need to change.
 */
@Component
@Slf4j
@Profile("!ci | thread")
@ConditionalOnProperty(prefix = "codepulse.notify", name = "enabled", havingValue = "true", matchIfMissing = true)
public class NotifyListener {

    private final ExecutorService vtpool;
    private final List<PgChannel> channels;

    private final Env env;
    private final UnifiedJedis jedis;
    private final Reporter reporter;

    private final LobbyNotifyHandler lobbyNotifyHandler;

    private JedisPubSub pubSub;

    public NotifyListener(
            final JedisClientManager jedisClientManager,
            final Reporter reporter,
            final Env env,
            final LobbyNotifyHandler lobbyNotifyHandler,
            final ExecutorService virtualPool) {
        this.channels = PgChannel.list();
        this.vtpool = virtualPool;
        this.reporter = reporter;
        this.env = env;
        this.jedis = jedisClientManager.getClient();
        this.lobbyNotifyHandler = lobbyNotifyHandler;
    }

    @PostConstruct
    protected void init() {
        vtpool.submit(this::listenLoop);
    }

    @PreDestroy
    protected void shutdown() {
        vtpool.shutdownNow();
        if (pubSub != null && pubSub.isSubscribed()) {
            try {
                pubSub.unsubscribe();
            } catch (Exception e) {
                log.error("Failed to unsubscribe from Redis pub/sub", e);
            }
        }
    }

    private void dispatch(final String channel, final String payload) {
        try {
            switch (PgChannel.fromChannelName(channel)) {
                case UPSERT_LOBBY -> lobbyNotifyHandler.handle(payload);
                default ->
                    throw new UnsupportedOperationException(
                            "a notification has been received that cannot be handled by the backend");
            }
        } catch (java.io.IOException e) {
            log.error("Failed to dispatch Redis pub/sub notification for channel {}", channel, e);
        }
    }

    private void listenLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                pubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(final String channel, final String message) {
                        dispatch(channel, message);
                    }
                };

                String[] channelNames =
                        channels.stream().map(PgChannel::getChannelName).toArray(String[]::new);

                log.info("Subscribing to Redis channels: {}", channels);
                // This call blocks until unsubscribe() is called or the connection is interrupted.
                jedis.subscribe(pubSub, channelNames);
            } catch (Exception e) {
                if (Thread.currentThread().isInterrupted()) {
                    log.info("Listener interrupted, closing...");
                    break;
                }

                log.error("Failed to listen to notifications", e);
                reporter.error(
                        "listenLoop",
                        Report.builder()
                                .environments(env.getActiveProfiles())
                                .location(Location.BACKEND)
                                .data(Reporter.throwableToString(e))
                                .build());

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
