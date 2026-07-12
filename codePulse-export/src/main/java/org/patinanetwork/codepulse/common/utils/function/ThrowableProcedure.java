package org.patinanetwork.codepulse.common.utils.function;

@FunctionalInterface
public interface ThrowableProcedure {
    void run() throws Exception;
}
