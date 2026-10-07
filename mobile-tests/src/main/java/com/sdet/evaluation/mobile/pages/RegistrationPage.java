package com.sdet.evaluation.mobile.pages;

import com.sdet.evaluation.mobile.config.MobileConfig;
import com.sdet.evaluation.mobile.models.UserRegistration;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import java.util.LinkedHashMap;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/** "Welcome to register a new User" screen ({@code RegisterUserActivity}). */
public class RegistrationPage extends BasePage {

  /** First text of the form (located by position, not by its text, so the text can be asserted). */
  private static final By WELCOME_TEXT =
      By.xpath("(//android.widget.ScrollView//android.widget.TextView)[1]");

  private static final By USERNAME = appId("inputUsername");
  private static final By EMAIL = appId("inputEmail");
  private static final By PASSWORD = appId("inputPassword");
  private static final By NAME = appId("inputName");
  private static final By LANGUAGE_SPINNER = appId("input_preferedProgrammingLanguage");
  private static final By SELECTED_LANGUAGE =
      By.xpath(
          "//*[@resource-id='%s:id/input_preferedProgrammingLanguage']/android.widget.TextView"
              .formatted(MobileConfig.appPackage()));
  private static final By ACCEPT_ADDS = appId("input_adds");
  private static final String REGISTER_BUTTON_ID = "btnRegisterUser";

  /** Key form elements by a readable name, in display order. */
  private static final Map<String, By> FORM_ELEMENTS = new LinkedHashMap<>();

  static {
    FORM_ELEMENTS.put("Username", USERNAME);
    FORM_ELEMENTS.put("E-Mail", EMAIL);
    FORM_ELEMENTS.put("Password", PASSWORD);
    FORM_ELEMENTS.put("Name", NAME);
    FORM_ELEMENTS.put("Programming language", LANGUAGE_SPINNER);
    FORM_ELEMENTS.put("I accept adds", ACCEPT_ADDS);
  }

  /** The screen's welcome text, e.g. {@code Welcome to register a new User}. */
  public String welcomeText() {
    return text(WELCOME_TEXT);
  }

  /**
   * Visibility of every form element plus the Register button (scrolled into view, as it is below
   * the fold on small screens), keyed by readable name.
   */
  public Map<String, Boolean> formElementVisibility() {
    visible(WELCOME_TEXT);
    var visibility = new LinkedHashMap<String, Boolean>();
    FORM_ELEMENTS.forEach((label, locator) -> visibility.put(label, isVisible(locator)));
    visibility.put("Register User", scrollToAppId(REGISTER_BUTTON_ID).isDisplayed());
    return visibility;
  }

  /** Current value of the Name field. */
  public String name() {
    return text(NAME);
  }

  /** Programming language currently shown by the spinner. */
  public String selectedProgrammingLanguage() {
    return text(SELECTED_LANGUAGE);
  }

  /** Fills every field and sets the "I accept adds" checkbox as requested. */
  @Step("Fill the registration form for user \"{registration.username}\"")
  public void fillForm(UserRegistration registration) {
    type(USERNAME, registration.username());
    type(EMAIL, registration.email());
    type(PASSWORD, registration.password());
    type(NAME, registration.name());
    hideKeyboard();
    selectProgrammingLanguage(registration.programmingLanguage());
    setAcceptAdds(registration.acceptAdds());
  }

  /** Opens the language spinner and picks an option by its text. */
  @Step("Select programming language \"{language}\"")
  public void selectProgrammingLanguage(String language) {
    tap(LANGUAGE_SPINNER);
    tap(
        AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"android:id/text1\").text(\"%s\")".formatted(language)));
  }

  /** Checks or unchecks "I accept adds" so it ends in the requested state. */
  @Step("Set \"I accept adds\" to {accept}")
  public void setAcceptAdds(boolean accept) {
    WebElement checkbox = visible(ACCEPT_ADDS);
    if (Boolean.parseBoolean(checkbox.getAttribute("checked")) != accept) {
      checkbox.click();
    }
  }

  /** {@code true} if "I accept adds" is checked. */
  public boolean isAcceptAddsChecked() {
    return Boolean.parseBoolean(visible(ACCEPT_ADDS).getAttribute("checked"));
  }

  /** Taps "Register User (verify)" and returns the confirmation screen. */
  @Step("Tap \"Register User\"")
  public RegistrationVerifyPage tapRegister() {
    scrollToAppId(REGISTER_BUTTON_ID).click();
    return new RegistrationVerifyPage();
  }
}
