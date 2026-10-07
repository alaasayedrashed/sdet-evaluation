package com.sdet.evaluation.mobile.pages;

import com.sdet.evaluation.mobile.config.MobileConfig;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * Hybrid "Web View Interaction" screen opened by the Chrome logo.
 *
 * <p>The native header is read in the {@code NATIVE_APP} context; the "Say Hello" demo page is
 * served by the app's embedded HTTP server and driven in the {@code WEBVIEW_*} context through
 * Chromedriver. Web methods switch context on demand, so callers never deal with it directly.
 */
public class WebViewPage extends BasePage {

    // Native
    private static final By HEADER = By.xpath("//*[@resource-id='%s:id/tableHeader']/android.widget.TextView"
            .formatted(MobileConfig.appPackage()));
    // Once content renders, the WebView node no longer exposes its resource id; its class is stable
    private static final By WEB_VIEW = By.className("android.webkit.WebView");

    // Web (Say Hello demo). CSS only: Chromedriver rejects the "id"/"name" strategies sent by the Appium client
    private static final By NAME_INPUT = By.cssSelector("#name_input");
    private static final By CAR_SELECT = By.cssSelector("select[name='car']");
    private static final By SUBMIT = By.cssSelector("input[type='submit']");
    private static final By BODY = By.cssSelector("body");
    private static final By START_AGAIN_LINK = By.linkText("here");

    /** Native header text of the screen, e.g. {@code Web View Interaction}. */
    public String header() {
        switchToNative();
        visible(WEB_VIEW);
        return text(HEADER);
    }

    @Step("Enter name \"{name}\" in the WebView")
    public void enterName(String name) {
        ensureWebView();
        type(NAME_INPUT, name);
    }

    @Step("Select preferred car \"{car}\" in the WebView")
    public void selectCar(String car) {
        ensureWebView();
        new Select(visible(CAR_SELECT)).selectByVisibleText(car);
    }

    /** Visible text of the car currently selected in the form. */
    public String selectedCar() {
        ensureWebView();
        return new Select(visible(CAR_SELECT)).getFirstSelectedOption().getText();
    }

    @Step("Tap \"Send me your name!\"")
    public void submit() {
        ensureWebView();
        tap(SUBMIT);
        await().until(ExpectedConditions.not(ExpectedConditions.presenceOfElementLocated(SUBMIT)));
    }

    /** Full visible text of the page, used to verify the echoed name and car. */
    public String pageText() {
        ensureWebView();
        return text(BODY);
    }

    @Step("Click the \"here\" link to start again")
    public void clickStartAgainLink() {
        ensureWebView();
        tap(START_AGAIN_LINK);
        visible(CAR_SELECT);
    }

    private void ensureWebView() {
        if (!inWebView()) {
            switchToWebView();
        }
    }
}
