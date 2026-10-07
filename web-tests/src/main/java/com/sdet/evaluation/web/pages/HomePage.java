package com.sdet.evaluation.web.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.sdet.evaluation.web.config.WebConfig;
import io.qameta.allure.Step;

/**
 * jqueryui.com landing page and its left sidebar navigation.
 */
public class HomePage extends BasePage {

    private static final String SIDEBAR_SECTION = "#sidebar aside";
    private static final String SECTION_TITLE = "h3.widget-title";
    private static final String DEMO_TITLE = "h1.entry-title";

    /** Opens the configured base URL. */
    @Step("Open jQuery UI home page")
    public void open() {
        page().navigate(WebConfig.baseUrl());
    }

    /**
     * Opens a demo the way a user does: by clicking its link in the left sidebar, under the given
     * section heading (e.g. section {@code Interactions}, demo {@code Droppable}). Waits until the
     * demo page title and its iframe are displayed.
     *
     * @param section sidebar section heading: Interactions, Widgets, Effects or Utilities
     * @param demo    link text in that section
     */
    @Step("Open demo \"{demo}\" from sidebar section \"{section}\"")
    public void openDemo(String section, String demo) {
        Locator sectionBlock = page().locator(SIDEBAR_SECTION).filter(new Locator.FilterOptions()
                .setHas(page().locator(SECTION_TITLE, new Page.LocatorOptions().setHasText(section))));
        sectionBlock.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName(demo).setExact(true)).click();

        page().locator(DEMO_TITLE).filter(new Locator.FilterOptions().setHasText(demo))
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        // Some demo bodies have no layout height, so wait for the frame document, not visibility
        demoFrame().locator("body").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED));
    }

    /** Title of the currently displayed demo page, e.g. {@code Droppable}. */
    public String demoTitle() {
        return page().locator(DEMO_TITLE).innerText().trim();
    }
}
