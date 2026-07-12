package org.patinanetwork.codepulse.common.reporter;

import com.google.common.annotations.VisibleForTesting;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;
import org.patinanetwork.codepulse.common.reporter.report.Report;
import org.patinanetwork.codepulse.common.reporter.throttled.ReportCounter;
import org.patinanetwork.codepulse.common.time.StandardizedLocalDateTime;
import org.patinanetwork.codepulse.common.time.StandardizedOffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Can either report an error or log data, written out via the application logger. */
@Component
@Primary
public class Reporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Reporter.class);

    private final Map<String, ReportCounter> counters = new ConcurrentHashMap<>();
    private static final int OCCURENCE_THRESHOLD = 3;
    private static final long TIME_LIMIT_MINUTES = 30;

    @VisibleForTesting
    protected long now() {
        return System.currentTimeMillis();
    }

    /** Convert the stacktrace of a {@linkplain Throwable} into a string. */
    public static String throwableToString(final Throwable throwable) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            throwable.printStackTrace(ps);
            ps.flush();
            return baos.toString();
        } catch (Exception e) {
            return "Failed to capture stack trace: " + e.getMessage();
        }
    }

    /**
     * Report an error.
     *
     * @param key a non-null identifer to group report types
     * @param report the detailed report object containing error metadata
     */
    @Async
    public void error(@NonNull String key, final Report report) {
        if (!shouldReport(key)) {
            return;
        }
        LOGGER.error(
                "An error occurred in CodePulse. Active environment(s): {} Current Time: {} Location: {}\n{}",
                report.getEnvironments(),
                StandardizedOffsetDateTime.now().toString(),
                report.getLocation().getResolvedName(),
                report.getData());
    }

    /**
     * Report a log.
     *
     * @param key a non-null identifer to group report types
     * @param report the detailed report object containing error metadata
     */
    @Async
    public void log(@NonNull String key, final Report report) {
        if (!shouldReport(key)) {
            return;
        }
        LOGGER.info(
                "Log request has been triggered. Active environment(s): {} Current Time: {} Location: {}\n{}",
                report.getEnvironments(),
                StandardizedLocalDateTime.now().toString(),
                report.getLocation().getResolvedName(),
                report.getData());
    }

    /**
     * Determine if a report should be sent. If threshold is reached within the time limit, the error will be reported.
     *
     * @param key a non-null identifer to group report types
     * @return {@code true} if the report should be sent, {@code false} otherwise
     */
    public boolean shouldReport(@NonNull String key) {
        long now = now();

        ReportCounter counter = counters.compute(key, (k, v) -> {
            if (v == null || now > v.getExpireTime()) {
                return new ReportCounter(
                        new AtomicInteger(1),
                        now + Duration.ofMinutes(TIME_LIMIT_MINUTES).toMillis());
            }
            v.getCount().incrementAndGet();
            return v;
        });

        if (counter.getCount().get() >= OCCURENCE_THRESHOLD) {
            return counters.remove(key, counter);
        }
        return false;
    }

    /** Clean up anything that hadn't been reported every 30 minutes. */
    @Scheduled(fixedRate = TIME_LIMIT_MINUTES, timeUnit = TimeUnit.MINUTES)
    public void cleanUp() {
        long now = now();
        counters.entrySet().removeIf(entry -> now > entry.getValue().getExpireTime());
    }
}
