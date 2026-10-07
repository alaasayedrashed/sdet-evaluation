package com.sdet.evaluation.web.hooks;

import com.microsoft.playwright.Page;
import com.sdet.evaluation.core.reporting.AllureUtils;
import com.sdet.evaluation.web.config.WebConfig;
import com.sdet.evaluation.web.driver.PlaywrightFactory;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Browser lifecycle and failure evidence for web scenarios.
 *
 * <ul>
 *     <li>{@code @Before}: start a fresh browser/context/page (with tracing if enabled)</li>
 *     <li>{@code @AfterStep}: optional screenshot after every step ({@code web.screenshot.each.step})</li>
 *     <li>{@code @After}: always a final screenshot; on failure also URL, page HTML and the
 *         Playwright trace; then close the browser no matter what happened</li>
 * </ul>
 */
public class WebHooks {

    private static final Logger LOG = LoggerFactory.getLogger(WebHooks.class);

    @Before
    public void startBrowser(Scenario scenario) {
        LOG.info("=== START: {} {}", scenario.getName(), scenario.getSourceTagNames());
        PlaywrightFactory.start();
    }

    @AfterStep
    public void screenshotAfterStep() {
        if (WebConfig.screenshotEachStep() && PlaywrightFactory.isStarted()) {
            attachScreenshot("Step screenshot");
        }
    }

    @After
    public void collectEvidenceAndCloseBrowser(Scenario scenario) {
        try {
            if (PlaywrightFactory.isStarted()) {
                boolean failed = scenario.isFailed();
                attachScreenshot(failed ? "Failure screenshot" : "Final screenshot");
                if (failed) {
                    attachPageDetails();
                }
                PlaywrightFactory.stopTracing(failed, traceFileName(scenario))
                        .ifPresent(trace -> AllureUtils.attachFile("Playwright trace (open at trace.playwright.dev)",
                                trace, "application/zip", "zip"));
            }
        } finally {
            PlaywrightFactory.stop();
            LOG.info("=== END ({}): {}", scenario.getStatus(), scenario.getName());
        }
    }

    private void attachScreenshot(String name) {
        try {
            AllureUtils.attachScreenshot(name, PlaywrightFactory.page().screenshot(new Page.ScreenshotOptions()
                    .setFullPage(true)
                    .setTimeout(WebConfig.screenshotTimeout().toMillis())));
        } catch (RuntimeException e) {
            LOG.warn("Could not capture screenshot '{}': {}", name, e.getMessage());
        }
    }

    private void attachPageDetails() {
        try {
            Page page = PlaywrightFactory.page();
            AllureUtils.attachText("Page URL", page.url());
            AllureUtils.attachHtml("Page HTML", page.content());
        } catch (RuntimeException e) {
            LOG.warn("Could not capture page details: {}", e.getMessage());
        }
    }

    private static String traceFileName(Scenario scenario) {
        return scenario.getName().replaceAll("[^A-Za-z0-9]+", "_") + "_" + System.currentTimeMillis() + ".zip";
    }
}
