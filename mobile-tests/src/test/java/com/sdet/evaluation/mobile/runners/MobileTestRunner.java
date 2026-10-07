package com.sdet.evaluation.mobile.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * TestNG entry point for the mobile feature files.
 *
 * <p>By default the intentional-failure scenarios ({@code @negative}) are excluded so the regular
 * build stays green. Run them explicitly with {@code -Dcucumber.filter.tags="@negative"}, or run
 * everything with {@code -Dcucumber.filter.tags="@mobile"}; the system property takes precedence
 * over the {@code tags} declared here.
 */
@CucumberOptions(
    features = "classpath:features",
    glue = "com.sdet.evaluation.mobile",
    tags = "@mobile and not @negative",
    plugin = {
      "pretty",
      "summary",
      "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
      "html:target/cucumber-reports/mobile.html",
      "json:target/cucumber-reports/mobile.json"
    },
    monochrome = true)
public class MobileTestRunner extends AbstractTestNGCucumberTests {}
