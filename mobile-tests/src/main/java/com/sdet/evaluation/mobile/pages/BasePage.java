package com.sdet.evaluation.mobile.pages;

import com.sdet.evaluation.mobile.config.MobileConfig;
import com.sdet.evaluation.mobile.driver.DriverFactory;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Set;

/**
 * Base class for every screen of the app under test.
 *
 * <p>Wraps the common actions in explicit waits (no implicit waits, no sleeps), provides
 * locator helpers for the app's resource ids, and the native/WebView context switching used by
 * hybrid screens. The driver is resolved lazily from {@link DriverFactory}, so page objects can
 * be created by dependency injection before the session exists.
 */
public abstract class BasePage {

    protected static final String NATIVE_CONTEXT = "NATIVE_APP";
    private static final String WEBVIEW_PREFIX = "WEBVIEW";
    /** Title bar of every activity of the app. */
    private static final By SCREEN_TITLE = By.id("android:id/title");

    private final Logger log = LoggerFactory.getLogger(getClass());

    /** The current thread's Appium driver. */
    protected AndroidDriver driver() {
        return DriverFactory.driver();
    }

    /** Locator for a view of the app under test by its short resource id, e.g. {@code buttonTest}. */
    protected static By appId(String resourceId) {
        return By.id(MobileConfig.appPackage() + ":id/" + resourceId);
    }

    /** Explicit wait with the default timeout ({@code mobile.timeout.explicit}). */
    protected WebDriverWait await() {
        return await(MobileConfig.explicitTimeout());
    }

    /** Explicit wait with a specific timeout. */
    protected WebDriverWait await(Duration timeout) {
        return new WebDriverWait(driver(), timeout);
    }

    /** Waits until the element is visible and returns it. */
    protected WebElement visible(By locator) {
        return await().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits until the element is clickable, then taps it. */
    protected void tap(By locator) {
        await().until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /** Replaces the text of an input field. */
    protected void type(By locator, String text) {
        WebElement field = visible(locator);
        field.clear();
        field.sendKeys(text);
    }

    /** Visible text of an element. */
    protected String text(By locator) {
        return visible(locator).getText();
    }

    /** {@code true} if the element becomes visible within the default timeout. */
    protected boolean isVisible(By locator) {
        return isVisible(locator, MobileConfig.explicitTimeout());
    }

    /** {@code true} if the element becomes visible within the given timeout. */
    protected boolean isVisible(By locator, Duration timeout) {
        try {
            await(timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException _) {
            return false;
        }
    }

    /** Waits until the element is gone (invisible or removed) and returns whether it disappeared. */
    protected boolean waitUntilGone(By locator, Duration timeout) {
        try {
            return await(timeout).until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException _) {
            return false;
        }
    }

    /** Scrolls the first scrollable container until the view with the given resource id is shown. */
    protected WebElement scrollToAppId(String resourceId) {
        String uiScrollable = ("new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().resourceId(\"%s:id/%s\"))").formatted(MobileConfig.appPackage(), resourceId);
        return await().until(driver -> driver.findElement(AppiumBy.androidUIAutomator(uiScrollable)));
    }

    /** Hides the soft keyboard if it is shown. */
    protected void hideKeyboard() {
        if (driver().isKeyboardShown()) {
            driver().hideKeyboard();
        }
    }

    /** Text of the activity title bar, e.g. {@code selendroid-test-app}. */
    public String screenTitle() {
        return text(SCREEN_TITLE);
    }

    /**
     * Waits for a WebView context, logs every available context and switches to the first WebView.
     *
     * @throws org.openqa.selenium.TimeoutException if no WebView appears within {@code mobile.timeout.webview}
     */
    @Step("Switch to WebView context")
    public void switchToWebView() {
        String webView = await(MobileConfig.webViewTimeout()).until(_ -> {
            Set<String> contexts = driver().getContextHandles();
            log.info("Available contexts: {}", contexts);
            return contexts.stream().filter(context -> context.startsWith(WEBVIEW_PREFIX)).findFirst().orElse(null);
        });
        driver().context(webView);
        log.info("Switched to context {}", webView);
    }

    /** Switches back to the native context (no-op if already native). */
    @Step("Switch to native context")
    public void switchToNative() {
        if (!NATIVE_CONTEXT.equals(driver().getContext())) {
            driver().context(NATIVE_CONTEXT);
            log.info("Switched to context {}", NATIVE_CONTEXT);
        }
    }

    /** {@code true} when the driver is currently in a WebView context. */
    protected boolean inWebView() {
        String context = driver().getContext();
        return context != null && context.startsWith(WEBVIEW_PREFIX);
    }
}
