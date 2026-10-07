package com.sdet.evaluation.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Immutable, thread-safe reader for a module's {@code .properties} file with layered overrides.
 *
 * <p>Every key is resolved in this order (first non-blank value wins):
 * <ol>
 *     <li>JVM system property, e.g. {@code -Dweb.headless=false}</li>
 *     <li>Environment variable, derived from the key by upper-casing it and replacing every
 *         non-alphanumeric character with {@code _}, e.g. {@code WEB_HEADLESS=false}</li>
 *     <li>The properties file loaded from the classpath</li>
 * </ol>
 * This lets CI and developers change URLs, credentials, timeouts or browser settings without
 * touching code or committed files.
 */
public final class ConfigReader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigReader.class);
    private static final Pattern DURATION = Pattern.compile("^(\\d+)\\s*(ms|s|m)?$");

    private final String source;
    private final Properties properties;

    private ConfigReader(String source, Properties properties) {
        this.source = source;
        this.properties = properties;
    }

    /**
     * Loads a properties file from the classpath (UTF-8).
     *
     * @param resourcePath classpath location, e.g. {@code config/api.properties}
     * @return a reader backed by that file
     * @throws ConfigurationException if the resource is missing or unreadable
     */
    public static ConfigReader fromClasspath(String resourcePath) {
        var properties = new Properties();
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new ConfigurationException("Configuration file not found on classpath: " + resourcePath);
            }
            properties.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ConfigurationException("Unable to read configuration file: " + resourcePath, e);
        }
        LOG.debug("Loaded {} properties from {}", properties.size(), resourcePath);
        return new ConfigReader(resourcePath, properties);
    }

    /**
     * Resolves an optional value using the override order described on the class.
     *
     * @param key property key
     * @return the resolved value, or empty if the key is not defined anywhere
     */
    public Optional<String> find(String key) {
        return firstNonBlank(System.getProperty(key))
                .or(() -> firstNonBlank(System.getenv(toEnvironmentKey(key))))
                .or(() -> firstNonBlank(properties.getProperty(key)))
                .map(String::trim);
    }

    /**
     * Resolves a mandatory value.
     *
     * @throws ConfigurationException if the key is not defined in any layer
     */
    public String get(String key) {
        return find(key).orElseThrow(() -> new ConfigurationException(
                "Missing required property '%s' (checked -D%s, env %s and %s)"
                        .formatted(key, key, toEnvironmentKey(key), source)));
    }

    /** Resolves a value, falling back to {@code defaultValue} when the key is not defined. */
    public String get(String key, String defaultValue) {
        return find(key).orElse(defaultValue);
    }

    /** Resolves a mandatory integer value. */
    public int getInt(String key) {
        return parse(key, get(key), Integer::parseInt);
    }

    /** Resolves an integer value with a default. */
    public int getInt(String key, int defaultValue) {
        return find(key).map(value -> parse(key, value, Integer::parseInt)).orElse(defaultValue);
    }

    /** Resolves a mandatory boolean value ({@code true}/{@code false}, case-insensitive). */
    public boolean getBoolean(String key) {
        return parseBoolean(key, get(key));
    }

    /** Resolves a boolean value with a default. */
    public boolean getBoolean(String key, boolean defaultValue) {
        return find(key).map(value -> parseBoolean(key, value)).orElse(defaultValue);
    }

    /**
     * Resolves a mandatory duration written as {@code <number>[ms|s|m]}, e.g. {@code 500ms},
     * {@code 15s} or {@code 2m}. A bare number is interpreted as seconds.
     */
    public Duration getDuration(String key) {
        return parseDuration(key, get(key));
    }

    /** Maps a property key to its environment-variable form: {@code web.base.url -> WEB_BASE_URL}. */
    static String toEnvironmentKey(String key) {
        return key.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "_");
    }

    private static Optional<String> firstNonBlank(String value) {
        return Optional.ofNullable(value).filter(v -> !v.isBlank());
    }

    private static boolean parseBoolean(String key, String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new ConfigurationException(
                    "Property '%s' must be true or false but was '%s'".formatted(key, value));
        };
    }

    private static Duration parseDuration(String key, String value) {
        Matcher matcher = DURATION.matcher(value.toLowerCase(Locale.ROOT));
        if (!matcher.matches()) {
            throw new ConfigurationException(
                    "Property '%s' must look like 500ms, 15s or 2m but was '%s'".formatted(key, value));
        }
        long amount = Long.parseLong(matcher.group(1));
        String unit = Optional.ofNullable(matcher.group(2)).orElse("s");
        return switch (unit) {
            case "ms" -> Duration.ofMillis(amount);
            case "m" -> Duration.ofMinutes(amount);
            default -> Duration.ofSeconds(amount);
        };
    }

    private static <T> T parse(String key, String value, Function<String, T> parser) {
        try {
            return parser.apply(value);
        } catch (RuntimeException e) {
            throw new ConfigurationException(
                    "Property '%s' has an invalid value '%s'".formatted(key, value), e);
        }
    }
}
