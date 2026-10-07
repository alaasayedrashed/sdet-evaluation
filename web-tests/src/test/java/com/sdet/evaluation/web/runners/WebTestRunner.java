package com.sdet.evaluation.web.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG entry point for the web feature files.
 *
 * <p>Scenarios run in parallel; the thread count comes from {@code -Dthreads=N} (default 4, see the
 * parent POM). Filter scenarios at run time with {@code -Dcucumber.filter.tags="@web_case4"}; the
 * system property takes precedence over the {@code tags} declared here.
 */
@CucumberOptions(
    features = "classpath:features",
    glue = "com.sdet.evaluation.web",
    tags = "@web",
    plugin = {
      "pretty",
      "summary",
      "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
      "html:target/cucumber-reports/web.html",
      "json:target/cucumber-reports/web.json"
    },
    monochrome = true)
public class WebTestRunner extends AbstractTestNGCucumberTests {

  @Override
  @DataProvider(parallel = true)
  public Object[][] scenarios() {
    return super.scenarios();
  }
}
