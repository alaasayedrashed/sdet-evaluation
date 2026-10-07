package com.sdet.evaluation.api.hooks;

import com.sdet.evaluation.api.context.ScenarioContext;
import com.sdet.evaluation.core.reporting.AllureUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scenario lifecycle for API tests. Every request/response is already attached by the
 * Allure REST Assured filter; on failure we additionally attach the last response status and
 * body, which Allure shows in the scenario's "Tear down" section next to the error.
 */
public class ApiHooks {

    private static final Logger LOG = LoggerFactory.getLogger(ApiHooks.class);

    private final ScenarioContext context;

    public ApiHooks(ScenarioContext context) {
        this.context = context;
    }

    @Before
    public void logScenarioStart(Scenario scenario) {
        LOG.info("=== START: {} {}", scenario.getName(), scenario.getSourceTagNames());
    }

    @After
    public void reportScenarioEnd(Scenario scenario) {
        if (scenario.isFailed()) {
            context.findLastResponse().ifPresent(response -> {
                AllureUtils.attachText("Last response status", response.getStatusLine());
                AllureUtils.attachJson("Last response body", response.asString());
            });
        }
        LOG.info("=== END ({}): {}", scenario.getStatus(), scenario.getName());
    }
}
