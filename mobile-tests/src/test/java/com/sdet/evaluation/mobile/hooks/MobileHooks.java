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
import org.openqa.selenium.logging.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Session lifecycle, app reset and failure evidence for mobile scenarios.
 *
 * <ul>
 *     <li>{@code @Before}: reuse (or recreate) the Appium session and relaunch the app, so every
 *         scenario starts on the home screen - even right after a crash</li>
 *     <li>{@code @AfterStep}: screenshot after every step of scenarios tagged {@code @screenshots}</li>
 *     <li>{@code @After}: final screenshot; on failure also app state, page source and logcat.
 *         Every capture is isolated in its own try/catch because, after a crash, any of them
 *         may fail - and evidence collection must never break the rest of the suite</li>
 *     <li>{@code @AfterAll}: end the Appium session</li>
 * </ul>
 */
public class MobileHooks {

    private static final Logger LOG = LoggerFactory.getLogger(MobileHooks.class);

    @Before
    public void launchApp(Scenario scenario) {
        LOG.info("=== START: {} {}", scenario.getName(), scenario.getSourceTagNames());
        AppLauncher.restartApp();
    }

    /** Screenshot after every step, only for scenarios tagged {@code @screenshots} (key flows). */
    @AfterStep("@screenshots")
    public void screenshotAfterStep() {
        attachScreenshot("Step screenshot");
    }

    @After
    public void collectEvidence(Scenario scenario) {
        if (!DriverFactory.hasDriver()) {
            return;
        }
        boolean failed = scenario.isFailed();
        attachScreenshot(failed ? "Failure screenshot" : "Final screenshot");
        if (failed) {
            attachFailureDetails();
        }
        LOG.info("=== END ({}): {}", scenario.getStatus(), scenario.getName());
    }

    @AfterAll
    public static void endSession() {
        DriverFactory.quit();
    }

    private void attachScreenshot(String name) {
        try {
            AllureUtils.attachScreenshot(name, ScreenCapture.png(DriverFactory.driver()));
        } catch (RuntimeException e) {
            LOG.warn("Could not capture screenshot '{}' (session may be unstable after a crash): {}", name, e.getMessage());
        }
    }

    private void attachFailureDetails() {
        AndroidDriver driver = DriverFactory.driver();
        try {
            AllureUtils.attachText("App state", "%s is %s".formatted(MobileConfig.appPackage(), AppLauncher.appState()));
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
            String tail = entries.stream()
                    .skip(Math.max(0, entries.size() - MobileConfig.logcatLines()))
                    .map(LogEntry::getMessage)
                    .collect(Collectors.joining(System.lineSeparator()));
            AllureUtils.attachText("Logcat (last %d lines)".formatted(MobileConfig.logcatLines()), tail);
        } catch (RuntimeException e) {
            LOG.warn("Could not capture logcat: {}", e.getMessage());
        }
    }
}
