package com.sdet.evaluation.web.steps;

import com.sdet.evaluation.web.pages.HomePage;
import io.cucumber.java.en.Given;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Shared navigation: every web scenario starts at jqueryui.com and opens a demo from the sidebar.
 */
public class NavigationSteps {

    private final HomePage homePage;

    public NavigationSteps(HomePage homePage) {
        this.homePage = homePage;
    }

    @Given("I am on the jQuery UI home page")
    public void openHomePage() {
        homePage.open();
    }

    @Given("I open the {string} demo from the {string} section of the sidebar")
    public void openDemo(String demo, String section) {
        homePage.openDemo(section, demo);

        assertThat(homePage.demoTitle()).as("title of the demo page opened from the sidebar").isEqualTo(demo);
    }
}
