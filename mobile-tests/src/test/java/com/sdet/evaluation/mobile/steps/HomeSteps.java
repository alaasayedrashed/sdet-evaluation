package com.sdet.evaluation.mobile.steps;

import com.sdet.evaluation.mobile.driver.AppLauncher;
import com.sdet.evaluation.mobile.pages.EndActivityDialog;
import com.sdet.evaluation.mobile.pages.HomePage;
import com.sdet.evaluation.mobile.pages.PopupWindow;
import io.appium.java_client.appmanagement.ApplicationState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Steps for the home screen and the widgets it opens (dialog, toast, popup, crash triggers).
 */
public class HomeSteps {

    private final HomePage homePage;

    private EndActivityDialog endActivityDialog;
    private PopupWindow popupWindow;
    private String toastText;

    public HomeSteps(HomePage homePage) {
        this.homePage = homePage;
    }

    @Given("the app is launched on the home screen")
    public void appIsLaunched() {
        assertThat(homePage.isDisplayed()).as("home screen shown after launch").isTrue();
    }

    @Then("the screen title should be {string}")
    public void screenTitleShouldBe(String expectedTitle) {
        assertThat(homePage.screenTitle()).as("activity title bar").isEqualTo(expectedTitle);
    }

    @Then("all key home screen elements should be displayed")
    public void keyElementsShouldBeDisplayed() {
        assertThat(homePage.keyElementVisibility())
                .as("visibility of each key home screen element")
                .allSatisfy((element, visible) -> assertThat(visible).as("'%s' is displayed", element).isTrue());
    }

    @Then("the home screen should be displayed")
    public void homeScreenShouldBeDisplayed() {
        assertAppInForeground("home screen");
        assertThat(homePage.isDisplayed()).as("home screen (EN Button) displayed").isTrue();
    }

    /**
     * Used by the intentional-failure scenarios: when the app has crashed, the message states the
     * app state explicitly instead of a generic "element not found".
     */
    @Then("the home screen should be displayed with the title {string}")
    public void homeScreenShouldShowTitle(String expectedTitle) {
        assertAppInForeground("home screen with title '%s'".formatted(expectedTitle));
        assertThat(homePage.screenTitle()).as("home screen title").isEqualTo(expectedTitle);
    }

    // ---------- EN Button dialog ----------

    @When("I tap the EN Button")
    public void tapEnButton() {
        endActivityDialog = homePage.tapEnButton();
    }

    @When("I choose {string} in the dialog")
    public void chooseInDialog(String option) {
        endActivityDialog.choose(option);
    }

    // ---------- Toast ----------

    @When("I tap {string} to show a toast")
    public void tapShowToast(String button) {
        toastText = homePage.tapShowToastAndReadIt();
    }

    @Then("a toast with the text {string} should be shown")
    public void toastShouldShow(String expectedText) {
        assertThat(toastText).as("text of the toast").isEqualTo(expectedText);
    }

    // ---------- Popup ----------

    @When("I tap {string} to open the popup")
    public void tapShowPopup(String button) {
        popupWindow = homePage.tapShowPopup();
    }

    @Then("the popup window should be displayed")
    public void popupShouldBeDisplayed() {
        assertThat(popupWindow.isDisplayed()).as("popup window (Dismiss button) displayed").isTrue();
    }

    @When("I dismiss the popup window")
    public void dismissPopup() {
        popupWindow.dismiss();
    }

    @Then("the popup window should be closed")
    public void popupShouldBeClosed() {
        assertThat(popupWindow.waitUntilClosed()).as("popup window closed after Dismiss").isTrue();
    }

    // ---------- Crash triggers (intentional failures) ----------

    @When("I tap {string} to throw an unhandled exception")
    public void tapCrashButton(String button) {
        homePage.tapCrashButton();
    }

    @When("I type {string} into the exception test field")
    public void typeIntoCrashField(String text) {
        homePage.typeIntoCrashField(text);
    }

    private void assertAppInForeground(String expectation) {
        ApplicationState state = AppLauncher.appState();
        assertThat(state)
                .as("Expected the %s, but the app is %s - it most likely crashed", expectation, state)
                .isEqualTo(ApplicationState.RUNNING_IN_FOREGROUND);
    }
}
