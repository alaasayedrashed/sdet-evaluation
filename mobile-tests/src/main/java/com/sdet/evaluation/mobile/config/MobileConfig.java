package com.sdet.evaluation.mobile.config;

import com.sdet.evaluation.core.config.ConfigReader;
import java.time.Duration;
import java.util.Optional;

/**
 * Typed access to {@code config/mobile.properties}. Every value can be overridden with {@code
 * -D<key>} or the matching environment variable (see {@link ConfigReader}).
 */
public final class MobileConfig {

  private static final ConfigReader CONFIG = ConfigReader.fromClasspath("config/mobile.properties");

  private MobileConfig() {}

  public static String appiumUrl() {
    return CONFIG.get("mobile.appium.url");
  }

  public static String platformName() {
    return CONFIG.get("mobile.platform.name");
  }

  public static String automationName() {
    return CONFIG.get("mobile.automation.name");
  }

  public static String deviceName() {
    return CONFIG.get("mobile.device.name");
  }

  /** Device serial to target when several devices are connected; empty = Appium picks one. */
  public static Optional<String> udid() {
    return CONFIG.find("mobile.udid");
  }

  /** Required Android version; empty = any. */
  public static Optional<String> platformVersion() {
    return CONFIG.find("mobile.platform.version");
  }

  /** Classpath location of the APK. */
  public static String appPath() {
    return CONFIG.get("mobile.app.path");
  }

  public static String appPackage() {
    return CONFIG.get("mobile.app.package");
  }

  public static String appActivity() {
    return CONFIG.get("mobile.app.activity");
  }

  public static boolean autoGrantPermissions() {
    return CONFIG.getBoolean("mobile.auto.grant.permissions");
  }

  public static Duration explicitTimeout() {
    return CONFIG.getDuration("mobile.timeout.explicit");
  }

  public static Duration toastTimeout() {
    return CONFIG.getDuration("mobile.timeout.toast");
  }

  public static Duration toastPollInterval() {
    return CONFIG.getDuration("mobile.timeout.toast.poll");
  }

  public static Duration progressTimeout() {
    return CONFIG.getDuration("mobile.timeout.progress");
  }

  public static Duration webViewTimeout() {
    return CONFIG.getDuration("mobile.timeout.webview");
  }

  public static Duration dialogProbeTimeout() {
    return CONFIG.getDuration("mobile.timeout.dialog.probe");
  }

  public static Duration newCommandTimeout() {
    return CONFIG.getDuration("mobile.timeout.new.command");
  }

  public static Duration serverInstallTimeout() {
    return CONFIG.getDuration("mobile.timeout.server.install");
  }

  public static Duration adbExecTimeout() {
    return CONFIG.getDuration("mobile.timeout.adb.exec");
  }

  public static int logcatLines() {
    return CONFIG.getInt("mobile.logcat.lines");
  }
}
