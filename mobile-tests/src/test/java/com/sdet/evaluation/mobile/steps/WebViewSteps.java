package com.sdet.evaluation.mobile.steps;

import com.sdet.evaluation.mobile.pages.HomePage;
import com.sdet.evaluation.mobile.pages.WebViewPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Steps for the hybrid "Web View Interaction" screen.
 */
public class WebViewSteps {

    private final HomePage homePage;
    private final WebViewPage webViewPage;

    /** Values entered on the form, kept to verify what the result page echoes. */
    private String enteredName;
    private String selectedCar;

    public WebViewSteps(HomePage homePage, WebViewPage webViewPage) {
        this.homePage = homePage;
        this.webViewPage = webViewPage;
    }

    @When("I tap the Chrome logo")
    public void tapChromeLogo() {
        homePage.tapChromeLogo();
    }

    @Then("the web view screen header should be {string}")
    public void headerShouldBe(String expectedHeader) {
        assertThat(webViewPage.header()).as("native header of the web view screen").isEqualTo(expectedHeader);
    }

    @Then("the web page should display {string}")
    public void pageShouldDisplay(String expectedText) {
        assertThat(webViewPage.pageText()).as("visible text of the page in the WebView").contains(expectedText);
    }

    @When("I enter the name {string} in the web page")
    public void enterName(String name) {
        enteredName = name;
        webViewPage.enterName(name);
    }

    @When("I select the preferred car {string} in the web page")
    public void selectCar(String car) {
        selectedCar = car;
        webViewPage.selectCar(car);
    }

    @When("I submit the web form with {string}")
    public void submitForm(String buttonLabel) {
        webViewPage.submit();
    }

    @Then("the web page should show the name and car I entered")
    public void pageShouldEchoNameAndCar() {
        String pageText = webViewPage.pageText();

        assertThat(pageText).as("result page should contain the entered name").contains(enteredName);
        assertThat(pageText).as("result page should contain the selected car").containsIgnoringCase(selectedCar);
    }

    @When("I click the {string} link in the web page")
    public void clickHereLink(String linkText) {
        webViewPage.clickStartAgainLink();
    }

    @Then("the preferred car should default to {string}")
    public void preferredCarShouldDefaultTo(String expectedCar) {
        assertThat(webViewPage.selectedCar()).as("default preferred car on a fresh form").isEqualTo(expectedCar);
    }
}
