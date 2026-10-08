package com.sdet.evaluation.api.hooks;

import com.sdet.evaluation.api.context.ScenarioContext;
import com.sdet.evaluation.core.reporting.AllureUtils;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scenario lifecycle for API tests. Every request/response is already attached by the Allure REST
 * Assured filter; on the failed step we additionally attach the last response status and body. They
 * are attached in {@code @AfterStep} so they appear on the test itself, not under the collapsed
 * "Tear down" section where Allure puts {@code @After} attachments.
 */
public class ApiHooks {

  private static final Logger LOG = LoggerFactory.getLogger(ApiHooks.class);

  private final ScenarioContext context;

  /** Hooks are created per scenario, so this only guards against attaching the evidence twice. */
  private boolean failureEvidenceAttached;

  public ApiHooks(ScenarioContext context) {
    this.context = context;
  }

  @Before
  public void logScenarioStart(Scenario scenario) {
    LOG.info("=== START: {} {}", scenario.getName(), scenario.getSourceTagNames());
  }

  @AfterStep
  public void evidenceAfterStep(Scenario scenario) {
    if (!scenario.isFailed() || failureEvidenceAttached) {
      return;
    }
    failureEvidenceAttached = true;
    context
        .findLastResponse()
        .ifPresent(
            response -> {
              AllureUtils.attachText("Last response status", response.getStatusLine());
              AllureUtils.attachJson("Last response body", response.asString());
            });
  }

  @After
  public void reportScenarioEnd(Scenario scenario) {
    LOG.info("=== END ({}): {}", scenario.getStatus(), scenario.getName());
  }
}
