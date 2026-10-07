package com.sdet.evaluation.mobile.pages;

import com.sdet.evaluation.mobile.config.MobileConfig;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** Confirmation dialog opened by "EN Button" ("This will end the activity"). */
public class EndActivityDialog extends BasePage {

  private static final By MESSAGE = By.id("android:id/message");

  /** Taps a dialog button by its label, e.g. {@code No, no} or {@code I agree}. */
  @Step("Choose \"{option}\" in the dialog")
  public void choose(String option) {
    tap(
        AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.Button\").text(\"%s\")"
                .formatted(option)));
    waitUntilGone(MESSAGE, MobileConfig.explicitTimeout());
  }
}
