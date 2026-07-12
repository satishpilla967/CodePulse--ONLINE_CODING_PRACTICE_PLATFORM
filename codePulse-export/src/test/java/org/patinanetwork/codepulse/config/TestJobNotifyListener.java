package org.patinanetwork.codepulse.config;

import org.patinanetwork.codepulse.common.env.Env;
import org.patinanetwork.codepulse.common.redis.JedisClientManager;
import org.patinanetwork.codepulse.common.reporter.Reporter;
import org.patinanetwork.codepulse.scheduled.pg.NotifyListener;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Can mock the {@code org.patinanetwork.codepulse.scheduled.pg.NotifyListener} so it won't start the loop on Spring
 * startup.
 */
@TestConfiguration
public class TestJobNotifyListener {

    private final JedisClientManager jedisClientManager;
    private final Reporter reporter;
    private final Env env;

    public TestJobNotifyListener(
            final JedisClientManager jedisClientManager, final Reporter reporter, final Env env) {
        this.jedisClientManager = jedisClientManager;
        this.reporter = reporter;
        this.env = env;
    }

    @Bean
    @Primary
    public NotifyListener notifyListener() {
        // loop should never start, and as such will also never stop.
        return new NotifyListener(jedisClientManager, reporter, env, null, null) {
            @Override
            protected void init() {
                return;
            }

            @Override
            protected void shutdown() {
                return;
            }
        };
    }
}
