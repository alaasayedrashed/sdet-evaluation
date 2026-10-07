package com.sdet.evaluation.mobile.pages;

import com.sdet.evaluation.mobile.config.MobileConfig;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Home screen ({@code HomeScreenActivity}).
 */
public class HomePage extends BasePage {

    private static final Logger LOG = LoggerFactory.getLogger(HomePage.class);

    private static final By EN_BUTTON = appId("buttonTest");
    private static final By CHROME_LOGO = appId("buttonStartWebview");
    private static final By FILE_LOGO = appId("startUserRegistration");
    private static final By TEXT_FIELD = appId("my_text_field");
    private static final By PROGRESS_BAR_BUTTON = appId("waitingButtonTest");
    private static final By DISPLAY_TEXT_VIEW_BUTTON = appId("visibleButtonTest");
    private static final By TOAST_BUTTON = appId("showToastButton");
    private static final By POPUP_BUTTON = appId("showPopupWindowButton");
    private static final By CRASH_BUTTON = appId("exceptionTestButton");
    private static final By CRASH_FIELD = appId("exceptionTestField");
    /** A toast node in the UiAutomator2 page source; group 1 is its text. */
    private static final Pattern TOAST_NODE = Pattern.compile("<android\\.widget\\.Toast\\b[^>]*\\btext=\"([^\"]+)\"");
    private static final By PROGRESS_DIALOG = By.id("android:id/progress");

    /** Key home-screen elements by a readable name, in display order. */
    private static final Map<String, By> KEY_ELEMENTS = new LinkedHashMap<>();

    static {
        KEY_ELEMENTS.put("EN Button", EN_BUTTON);
        KEY_ELEMENTS.put("Chrome logo", CHROME_LOGO);
        KEY_ELEMENTS.put("File logo", FILE_LOGO);
        KEY_ELEMENTS.put("Text field", TEXT_FIELD);
        KEY_ELEMENTS.put("Show Progress Bar for a while", PROGRESS_BAR_BUTTON);
        KEY_ELEMENTS.put("Display text view", DISPLAY_TEXT_VIEW_BUTTON);
        KEY_ELEMENTS.put("Displays a Toast", TOAST_BUTTON);
        KEY_ELEMENTS.put("Display Popup Window", POPUP_BUTTON);
        KEY_ELEMENTS.put("Press to throw unhandled exception", CRASH_BUTTON);
        KEY_ELEMENTS.put("Type to throw unhandled exception", CRASH_FIELD);
    }

    /** {@code true} if the home screen is shown (its EN Button is visible). */
    public boolean isDisplayed() {
        return isVisible(EN_BUTTON);
    }

    /** Visibility of every key home-screen element, keyed by readable name. */
    public Map<String, Boolean> keyElementVisibility() {
        visible(EN_BUTTON);
        var visibility = new LinkedHashMap<String, Boolean>();
        KEY_ELEMENTS.forEach((name, locator) -> visibility.put(name, isVisible(locator)));
        return visibility;
    }

    @Step("Tap \"EN Button\"")
    public EndActivityDialog tapEnButton() {
        tap(EN_BUTTON);
        return new EndActivityDialog();
    }

    @Step("Tap the Chrome logo")
    public WebViewPage tapChromeLogo() {
        tap(CHROME_LOGO);
        return new WebViewPage();
    }

    @Step("Tap the File logo")
    public RegistrationPage tapFileLogo() {
        tap(FILE_LOGO);
        return new RegistrationPage();
    }

    /**
     * Taps "Show Progress Bar for a while" and waits (explicitly, up to {@code mobile.timeout.progress})
     * for the progress dialog to appear and disappear; the app then opens the registration screen.
     *
     * @throws IllegalStateException if the progress dialog does not disappear in time
     */
    @Step("Tap \"Show Progress Bar for a while\" and wait for the loader to disappear")
    public RegistrationPage tapShowProgressBarAndWait() {
        tap(PROGRESS_BAR_BUTTON);
        visible(PROGRESS_DIALOG);
        if (!waitUntilGone(PROGRESS_DIALOG, MobileConfig.progressTimeout())) {
            throw new IllegalStateException("Progress dialog still visible after " + MobileConfig.progressTimeout());
        }
        return new RegistrationPage();
    }

    /**
     * Taps "Displays a Toast" and captures the toast text.
     *
     * <p>Toasts disappear after ~2 seconds and UiAutomator2 exposes them only as a short-lived
     * cached {@code android.widget.Toast} node, so "find element, then read its text" can lose the
     * race. Instead the page source (which contains the toast node while it is shown) is polled
     * quickly and the text is extracted in the same call.
     */
    @Step("Tap \"Displays a Toast\" and capture the toast")
    public String tapShowToastAndReadIt() {
        tap(TOAST_BUTTON);
        return new FluentWait<>(driver())
                .withTimeout(MobileConfig.toastTimeout())
                .pollingEvery(MobileConfig.toastPollInterval())
                .withMessage("toast (%s) to appear".formatted(TOAST_NODE.pattern()))
                .until(driver -> {
                    Matcher toast = TOAST_NODE.matcher(driver.getPageSource());
                    return toast.find() ? toast.group(1) : null;
                });
    }

    @Step("Tap \"Display Popup Window\"")
    public PopupWindow tapShowPopup() {
        tap(POPUP_BUTTON);
        return new PopupWindow();
    }

    @Step("Tap \"Press to throw unhandled exception\"")
    public void tapCrashButton() {
        tap(CRASH_BUTTON);
    }

    /**
     * Types into the field that makes the app throw on text change. The app dies while the keys are
     * being sent, so the field going stale is the expected outcome and is only logged; the crash
     * itself is then reported by the scenario's verification step with a clear message.
     */
    @Step("Type \"{text}\" into \"Type to throw unhandled exception\"")
    public void typeIntoCrashField(String text) {
        WebElement field = visible(CRASH_FIELD);
        try {
            field.sendKeys(text);
        } catch (StaleElementReferenceException e) {
            LOG.warn("Field disappeared while typing '{}' - the app most likely crashed", text);
        }
    }
}
