package com.sdet.evaluation.mobile.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.sdet.evaluation.mobile.models.UserRegistration;
import com.sdet.evaluation.mobile.pages.HomePage;
import com.sdet.evaluation.mobile.pages.RegistrationPage;
import com.sdet.evaluation.mobile.pages.RegistrationVerifyPage;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;

/**
 * Steps for user registration: the form, its confirmation screen and the progress-bar entry point.
 */
public class RegistrationSteps {

  private final HomePage homePage;
  private final RegistrationPage registrationPage;
  private final RegistrationVerifyPage verifyPage;

  /** What was typed into the form, compared later with the confirmation screen. */
  private UserRegistration enteredRegistration;

  public RegistrationSteps(
      HomePage homePage, RegistrationPage registrationPage, RegistrationVerifyPage verifyPage) {
    this.homePage = homePage;
    this.registrationPage = registrationPage;
    this.verifyPage = verifyPage;
  }

  @DataTableType
  public UserRegistration userRegistration(Map<String, String> row) {
    return new UserRegistration(
        row.get("username"),
        row.get("email"),
        row.get("password"),
        row.get("name"),
        row.get("programming language"),
        Boolean.parseBoolean(row.get("accept adds")));
  }

  @When("I tap the File logo")
  public void tapFileLogo() {
    homePage.tapFileLogo();
  }

  @When("I tap {string} and wait for the loader to disappear")
  public void tapProgressBarAndWait(String button) {
    homePage.tapShowProgressBarAndWait();
  }

  @Then("the registration screen should show {string}")
  public void registrationScreenShouldShow(String expectedWelcome) {
    assertThat(registrationPage.welcomeText())
        .as("registration welcome text")
        .isEqualTo(expectedWelcome);
  }

  @Then("all registration form elements should be displayed")
  public void formElementsShouldBeDisplayed() {
    assertThat(registrationPage.formElementVisibility())
        .as("visibility of each registration form element")
        .allSatisfy(
            (element, visible) -> assertThat(visible).as("'%s' is displayed", element).isTrue());
  }

  @Then("the Name field should be pre-filled with {string}")
  public void nameShouldBePrefilled(String expectedName) {
    assertThat(registrationPage.name()).as("pre-filled Name field").isEqualTo(expectedName);
  }

  @Then("the default programming language should be {string}")
  public void defaultLanguageShouldBe(String expectedLanguage) {
    assertThat(registrationPage.selectedProgrammingLanguage())
        .as("default programming language")
        .isEqualTo(expectedLanguage);
  }

  @When("I fill the registration form with:")
  public void fillRegistrationForm(UserRegistration registration) {
    enteredRegistration = registration;
    registrationPage.fillForm(registration);
    assertThat(registrationPage.isAcceptAddsChecked())
        .as("'I accept adds' checkbox state after filling the form")
        .isEqualTo(registration.acceptAdds());
  }

  @When("I tap Register User")
  public void tapRegister() {
    registrationPage.tapRegister();
  }

  @Then("the confirmation screen should show the details I entered")
  public void confirmationShouldShowEnteredDetails() {
    assertThat(verifyPage.displayedRegistration())
        .as("details on the 'Verify user' screen")
        .isEqualTo(enteredRegistration);
  }

  @When("I confirm the registration with Register User")
  public void confirmRegistration() {
    verifyPage.confirm();
  }
}
