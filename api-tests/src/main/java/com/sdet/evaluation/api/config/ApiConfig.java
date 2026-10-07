package com.sdet.evaluation.api.config;

import com.sdet.evaluation.core.config.ConfigReader;
import java.time.Duration;
import java.util.Optional;

/**
 * Typed access to {@code config/api.properties}. Every value can be overridden with {@code -D<key>}
 * or the matching environment variable (see {@link ConfigReader}).
 */
public final class ApiConfig {

  private static final ConfigReader CONFIG = ConfigReader.fromClasspath("config/api.properties");

  private ApiConfig() {}

  /** Scheme + host, e.g. {@code https://reqres.in}. */
  public static String baseUri() {
    return CONFIG.get("api.base.uri");
  }

  /** Path prefix shared by all endpoints, e.g. {@code /api}. */
  public static String basePath() {
    return CONFIG.get("api.base.path");
  }

  /** Name of the API-key header, e.g. {@code x-api-key}. */
  public static String apiKeyHeader() {
    return CONFIG.get("api.key.header");
  }

  /**
   * API key value; empty when the key should not be sent (e.g. {@code -Dapi.key.enabled=false}).
   */
  public static Optional<String> apiKey() {
    return CONFIG.getBoolean("api.key.enabled", true) ? CONFIG.find("api.key") : Optional.empty();
  }

  /** Maximum time to establish a connection. */
  public static Duration connectTimeout() {
    return CONFIG.getDuration("api.timeout.connect");
  }

  /** Maximum time to wait for response data. */
  public static Duration readTimeout() {
    return CONFIG.getDuration("api.timeout.read");
  }
}
