package com.sdet.evaluation.core.config;

/**
 * Framework-wide settings shared by every module (backed by {@code config/framework.properties}).
 */
public final class FrameworkConfig {

    private static final ConfigReader CONFIG = ConfigReader.fromClasspath("config/framework.properties");

    private FrameworkConfig() {
    }

    /** How many times a failed test is re-run before it is reported as failed ({@code -Dretry.count=1}). */
    public static int retryCount() {
        return CONFIG.getInt("retry.count", 0);
    }
}
