package org.patinanetwork.codepulse.common.potd.leetcode;

public class LeetcodePotdClientException extends RuntimeException {
    public LeetcodePotdClientException(final String message) {
        super(message);
    }

    public LeetcodePotdClientException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
