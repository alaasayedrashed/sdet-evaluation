package com.sdet.evaluation.web.pages;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import java.util.List;

/**
 * Utilities / Widget Factory demo (custom "colorize" widget).
 */
public class WidgetFactoryPage extends BasePage {

    private Locator colorizedWidgets() {
        return demoFrame().locator(".custom-colorize");
    }

    @Step("Click button \"{label}\"")
    public void clickButton(String label) {
        demoFrame().getByRole(AriaRole.BUTTON, new FrameLocator.GetByRoleOptions()
                .setName(label).setExact(true)).click();
    }

    /** Computed {@code background-color} of every colorized widget, in DOM order. */
    public List<String> widgetBackgroundColors() {
        Locator widgets = colorizedWidgets();
        widgets.first().waitFor();
        return widgets.all().stream()
                .map(widget -> computedStyle(widget, "background-color"))
                .toList();
    }
}
