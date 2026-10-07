package com.sdet.evaluation.core.utils;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates unique, readable test data so repeated or parallel runs never collide.
 */
public final class TestDataGenerator {

    private static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyz0123456789";

    private TestDataGenerator() {
    }

    /** A short random lowercase alphanumeric suffix, e.g. {@code k3x9q1}. */
    public static String randomSuffix(int length) {
        var random = ThreadLocalRandom.current();
        var suffix = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            suffix.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return suffix.toString();
    }

    /** A unique value built from a prefix, e.g. {@code uniqueValue("user") -> user_k3x9q1}. */
    public static String uniqueValue(String prefix) {
        return prefix + "_" + randomSuffix(6);
    }

    /** A unique e-mail address on the given domain, e.g. {@code qa_k3x9q1@example.com}. */
    public static String uniqueEmail(String prefix, String domain) {
        return (uniqueValue(prefix) + "@" + domain).toLowerCase(Locale.ROOT);
    }
}
