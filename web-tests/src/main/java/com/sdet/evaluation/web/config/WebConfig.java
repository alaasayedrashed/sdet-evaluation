package com.sdet.evaluation.web.config;

import com.sdet.evaluation.core.config.ConfigReader;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Typed access to {@code config/web.properties}. Every value can be overridden with {@code -D<key>}
 * or the matching environment variable (see {@link ConfigReader}).
 */
public final class WebConfig {

  private static final ConfigReader CONFIG = ConfigReader.fromClasspath("config/web.properties");

  private WebConfig() {}

  public static String baseUrl() {
    return CONFIG.get("web.base.url");
  }

  /** {@code chromium}, {@code firefox} or {@code webkit}. */
  public static String browser() {
    return CONFIG.get("web.browser");
  }

  public static boolean headless() {
    return CONFIG.getBoolean("web.headless");
  }

  public static Duration slowMo() {
    return CONFIG.getDuration("web.slow.mo");
  }

  public static int viewportWidth() {
    return CONFIG.getInt("web.viewport.width");
  }

  public static int viewportHeight() {
    return CONFIG.getInt("web.viewport.height");
  }

  /** Upper bound for Playwright auto-waiting on actions and locators. */
  public static Duration defaultTimeout() {
    return CONFIG.getDuration("web.timeout.default");
  }

  public static Duration navigationTimeout() {
    return CONFIG.getDuration("web.timeout.navigation");
  }

  /** Budget for capturing a screenshot, independent of the action timeout. */
  public static Duration screenshotTimeout() {
    return CONFIG.getDuration("web.timeout.screenshot");
  }

  /** Number of intermediate mouse moves used for drag operations. */
  public static int dragSteps() {
    return CONFIG.getInt("web.drag.steps");
  }

  public static boolean traceOnFailure() {
    return CONFIG.getBoolean("web.trace.on.failure");
  }

  /** Where traces and other Playwright artifacts are written. */
  public static Path artifactsDir() {
    return Path.of(CONFIG.get("web.artifacts.dir"));
  }
}
