package com.sdet.evaluation.mobile.pages;

import com.sdet.evaluation.mobile.models.UserRegistration;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** "Verify user" confirmation screen shown after submitting the registration form. */
public class RegistrationVerifyPage extends BasePage {

  private static final By NAME = appId("label_name_data");
  private static final By USERNAME = appId("label_username_data");
  private static final By PASSWORD = appId("label_password_data");
  private static final By EMAIL = appId("label_email_data");
  private static final By LANGUAGE = appId("label_preferedProgrammingLanguage_data");
  private static final By ACCEPT_ADDS = appId("label_acceptAdds_data");
  private static final By REGISTER_BUTTON = appId("buttonRegisterUser");

  /** Reads every value shown on the confirmation screen. */
  public UserRegistration displayedRegistration() {
    visible(REGISTER_BUTTON);
    return new UserRegistration(
        text(USERNAME),
        text(EMAIL),
        text(PASSWORD),
        text(NAME),
        text(LANGUAGE),
        Boolean.parseBoolean(text(ACCEPT_ADDS)));
  }

  /** Taps "Register User" to confirm; the app returns to the home screen. */
  @Step("Confirm with \"Register User\"")
  public HomePage confirm() {
    tap(REGISTER_BUTTON);
    return new HomePage();
  }
}
