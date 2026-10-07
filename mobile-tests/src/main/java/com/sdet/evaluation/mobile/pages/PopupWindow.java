package com.sdet.evaluation.mobile.pages;

import com.sdet.evaluation.mobile.config.MobileConfig;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * The PopupWindow shown by "Display Popup Window". It lives in its own window, which is why the
 * session enables {@code appium:enableMultiWindows}.
 */
public class PopupWindow extends BasePage {

    private static final By DISMISS = appId("popup_dismiss_button");

    /** {@code true} if the popup's Dismiss button is visible. */
    public boolean isDisplayed() {
        return isVisible(DISMISS);
    }

    @Step("Tap \"Dismiss\" on the popup")
    public void dismiss() {
        tap(DISMISS);
    }

    /** Waits until the popup is gone; returns {@code false} if it is still shown after the timeout. */
    public boolean waitUntilClosed() {
        return waitUntilGone(DISMISS, MobileConfig.explicitTimeout());
    }
}
