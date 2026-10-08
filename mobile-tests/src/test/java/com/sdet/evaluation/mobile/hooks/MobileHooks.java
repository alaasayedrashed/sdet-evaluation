package com.sdet.evaluation.mobile.hooks;

import com.sdet.evaluation.core.reporting.AllureUtils;
import com.sdet.evaluation.mobile.config.MobileConfig;
import com.sdet.evaluation.mobile.driver.AppLauncher;
import com.sdet.evaluation.mobile.driver.DriverFactory;
import com.sdet.evaluation.mobile.driver.ScreenCapture;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.logging.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session lifecycle, app reset and failure evidence for mobile scenarios.
 *
 * <ul>
 *   <li>{@code @Before}: reuse (or recreate) the Appium session and relaunch the app, so every
 *       scenario starts on the home screen - even right after a crash
 *   <li>{@code @AfterStep}: on the failed step, the failure screenshot, app state, page source and
 *       logcat; otherwise a step screenshot for scenarios tagged {@code @screenshots}. Attaching
 *       here puts the evidence on the test itself: Allure reports {@code @After} attachments under
 *       the collapsed "Tear down" section, where screenshots are easy to miss
 *   <li>{@code @After}: final screenshot on success; failure evidence if no step ran
 *   <li>{@code @AfterAll}: close the app and end the Appium session
 * </ul>
 *
 * Every capture is isolated in its own try/catch because, after a crash, any of them may fail - and
 * evidence collection must never break the rest of the suite.
 */
public class MobileHooks {

  private static final Logger LOG = LoggerFactory.getLogger(MobileHooks.class);
  private static final String SCREENSHOTS_TAG = "@screenshots";

  /** Hooks are created per scenario, so this only guards against attaching the evidence twice. */
  private boolean failureEvidenceAttached;

  @Before
  public void launchApp(Scenario scenario) {
    LOG.info("=== START: {} {}", scenario.getName(), scenario.getSourceTagNames());
    AppLauncher.restartApp();
  }

  @AfterStep
  public void evidenceAfterStep(Scenario scenario) {
    if (!DriverFactory.hasDriver()) {
      return;
    }
    if (scenario.isFailed()) {
      attachFailureEvidence();
    } else if (scenario.getSourceTagNames().contains(SCREENSHOTS_TAG)) {
      attachScreenshot("Step screenshot");
    }
  }

  @After
  public void collectEvidence(Scenario scenario) {
    if (!DriverFactory.hasDriver()) {
      return;
    }
    if (scenario.isFailed()) {
      // No-op when @AfterStep already attached it; covers failures before any step ran
      attachFailureEvidence();
    } else {
      attachScreenshot("Final screenshot");
    }
    LOG.info("=== END ({}): {}", scenario.getStatus(), scenario.getName());
  }

  @AfterAll
  public static void endSession() {
    DriverFactory.quit();
  }

  private void attachFailureEvidence() {
    if (failureEvidenceAttached) {
      return;
    }
    failureEvidenceAttached = true;
    attachScreenshot("Failure screenshot");
    attachFailureDetails();
  }

  private void attachScreenshot(String name) {
    try {
      AllureUtils.attachScreenshot(name, ScreenCapture.png(DriverFactory.driver()));
    } catch (RuntimeException e) {
      LOG.warn(
          "Could not capture screenshot '{}' (session may be unstable after a crash): {}",
          name,
          e.getMessage());
      // Leave a visible trace in the report instead of a silently missing image
      AllureUtils.attachText(name + " unavailable", String.valueOf(e.getMessage()));
    }
  }

  private void attachFailureDetails() {
    AndroidDriver driver = DriverFactory.driver();
    try {
      AllureUtils.attachText(
          "App state", "%s is %s".formatted(MobileConfig.appPackage(), AppLauncher.appState()));
    } catch (RuntimeException e) {
      LOG.warn("Could not read app state: {}", e.getMessage());
    }
    try {
      AllureUtils.attachXml("Page source", driver.getPageSource());
    } catch (RuntimeException e) {
      LOG.warn("Could not capture page source: {}", e.getMessage());
    }
    try {
      List<LogEntry> entries = driver.manage().logs().get("logcat").getAll();
      String tail =
          entries.stream()
              .skip(Math.max(0, entries.size() - MobileConfig.logcatLines()))
              .map(LogEntry::getMessage)
              .collect(Collectors.joining(System.lineSeparator()));
      AllureUtils.attachText("Logcat (last %d lines)".formatted(MobileConfig.logcatLines()), tail);
    } catch (RuntimeException e) {
      LOG.warn("Could not capture logcat: {}", e.getMessage());
    }
  }
}
