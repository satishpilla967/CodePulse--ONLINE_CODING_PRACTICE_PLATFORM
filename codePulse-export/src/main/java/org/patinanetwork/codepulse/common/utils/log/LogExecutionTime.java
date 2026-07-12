package org.patinanetwork.codepulse.common.utils.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to log the execution time of a method.
 *
 * <p>When applied to a method, logs the execution time to SLF4J by default. Optionally, if {@code report} is set to
 * {@code true}, the execution time will also be sent through the {@link org.patinanetwork.codepulse.common.reporter.Reporter}
 * for monitoring purposes.
 *
 * <p><b>Example usage:</b>
 *
 * <pre>{@code
 * @LogExecutionTime(report = true)
 * public void processData() {
 *     // method implementation
 * }
 * }</pre>
 *
 * @apiNote <b>Performance implications:</b> Reporting is expensive, so <b>use it sparingly</b>.
 * @see LogExecutionTimeAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
    boolean report() default false;
}
