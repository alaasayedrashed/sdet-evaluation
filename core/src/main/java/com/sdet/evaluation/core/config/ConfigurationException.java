package com.sdet.evaluation.core.config;

/**
 * Thrown when configuration is missing or malformed. Unchecked on purpose: a broken
 * configuration is a setup error that should fail fast with a clear message.
 */
public class ConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
