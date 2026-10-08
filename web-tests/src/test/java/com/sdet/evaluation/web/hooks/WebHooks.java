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
 *   <li>{@code @Before}: start a fresh browser/context/page (with tracing if enabled)
 *   <li>{@code @AfterStep}: on the failed step, the failure screenshot, URL and page HTML;
 *       otherwise a step screenshot for scenarios tagged {@code @screenshots}. Attaching here puts
 *       the evidence on the test itself: Allure reports {@code @After} attachments under the
 *       collapsed "Tear down" section, where screenshots are easy to miss
 *   <li>{@code @After}: final screenshot on success, the Playwright trace on failure; then close
 *       the browser no matter what happened
 * </ul>
 */
public class WebHooks {

  private static final Logger LOG = LoggerFactory.getLogger(WebHooks.class);
  private static final String SCREENSHOTS_TAG = "@screenshots";

  /** Hooks are created per scenario, so this only guards against attaching the evidence twice. */
  private boolean failureEvidenceAttached;

  @Before
  public void startBrowser(Scenario scenario) {
    LOG.info("=== START: {} {}", scenario.getName(), scenario.getSourceTagNames());
    PlaywrightFactory.start();
  }

  @AfterStep
  public void evidenceAfterStep(Scenario scenario) {
    if (!PlaywrightFactory.isStarted()) {
      return;
    }
    if (scenario.isFailed()) {
      attachFailureEvidence();
    } else if (scenario.getSourceTagNames().contains(SCREENSHOTS_TAG)) {
      attachScreenshot("Step screenshot");
    }
  }

  @After
  public void collectEvidenceAndCloseBrowser(Scenario scenario) {
    try {
      if (PlaywrightFactory.isStarted()) {
        boolean failed = scenario.isFailed();
        if (failed) {
          // No-op when @AfterStep already attached it; covers failures before any step ran
          attachFailureEvidence();
        } else {
          attachScreenshot("Final screenshot");
        }
        PlaywrightFactory.stopTracing(failed, traceFileName(scenario))
            .ifPresent(
                trace ->
                    AllureUtils.attachFile(
                        "Playwright trace (open at trace.playwright.dev)",
                        trace,
                        "application/zip",
                        "zip"));
      }
    } finally {
      PlaywrightFactory.stop();
      LOG.info("=== END ({}): {}", scenario.getStatus(), scenario.getName());
    }
  }

  private void attachFailureEvidence() {
    if (failureEvidenceAttached) {
      return;
    }
    failureEvidenceAttached = true;
    attachScreenshot("Failure screenshot");
    attachPageDetails();
  }

  private void attachScreenshot(String name) {
    try {
      AllureUtils.attachScreenshot(
          name,
          PlaywrightFactory.page()
              .screenshot(
                  new Page.ScreenshotOptions()
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
    return scenario.getName().replaceAll("[^A-Za-z0-9]+", "_")
        + "_"
        + System.currentTimeMillis()
        + ".zip";
  }
}
