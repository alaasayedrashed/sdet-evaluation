package com.sdet.evaluation.api.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG entry point for the API feature files.
 *
 * <p>Scenarios run in parallel; the thread count comes from {@code -Dthreads=N} (default 4, see the
 * parent POM). Filter scenarios at run time with {@code -Dcucumber.filter.tags="@smoke"}; the
 * system property takes precedence over the {@code tags} declared here.
 */
@CucumberOptions(
    features = "classpath:features",
    glue = "com.sdet.evaluation.api",
    tags = "@api",
    plugin = {
      "pretty",
      "summary",
      "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
      "html:target/cucumber-reports/api.html",
      "json:target/cucumber-reports/api.json"
    },
    monochrome = true)
public class ApiTestRunner extends AbstractTestNGCucumberTests {

  @Override
  @DataProvider(parallel = true)
  public Object[][] scenarios() {
    return super.scenarios();
  }
}
