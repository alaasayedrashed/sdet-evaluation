package com.sdet.evaluation.mobile.driver;

import com.sdet.evaluation.mobile.config.MobileConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.appmanagement.ApplicationState;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Brings the app under test to a clean, known state before every scenario.
 *
 * <p>The app is force-stopped and relaunched (terminate + activate), which resets its in-memory
 * state and also recovers from a crash in the previous scenario. Because the app targets
 * Android SDK 10, Android can show system screens before it on first launch; these are
 * dismissed here so scenarios always start on the home screen.
 */
public final class AppLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(AppLauncher.class);
    private static final String NATIVE_CONTEXT = "NATIVE_APP";

    /**
     * A system screen that may cover the app, and the button that dismisses it.
     *
     * @param description human-readable name for logs
     * @param marker      element proving the screen is shown
     * @param dismiss     element to tap to dismiss it
     */
    private record SystemScreen(String description, By marker, By dismiss) {
    }

    private static final List<SystemScreen> SYSTEM_SCREENS = List.of(
            // Android 10+: permission review shown for apps that target SDK < 23
            new SystemScreen("legacy permission review",
                    By.id("com.android.permissioncontroller:id/continue_button"),
                    By.id("com.android.permissioncontroller:id/continue_button")),
            // "This app was built for an older version of Android" warning
            new SystemScreen("old target SDK warning",
                    By.xpath("//*[@resource-id='android:id/message' and contains(@text, 'older version of Android')]"),
                    By.id("android:id/button1")),
            // "<app> has stopped" / "keeps stopping" dialog left over from a crash
            new SystemScreen("app crash dialog",
                    By.id("android:id/aerr_close"),
                    By.id("android:id/aerr_close")));

    private AppLauncher() {
    }

    /** Force-stops (if running) and relaunches the app, then clears any blocking system screens. */
    public static void restartApp() {
        AndroidDriver driver = DriverFactory.getOrCreate();
        driver.context(NATIVE_CONTEXT);
        String appPackage = MobileConfig.appPackage();

        ApplicationState state = driver.queryAppState(appPackage);
        if (state != ApplicationState.NOT_RUNNING && state != ApplicationState.NOT_INSTALLED) {
            driver.terminateApp(appPackage);
        }
        driver.activateApp(appPackage);
        LOG.info("App {} (re)launched; previous state was {}", appPackage, state);
        dismissSystemScreens(driver);
    }

    /** Current state of the app under test, e.g. RUNNING_IN_FOREGROUND or NOT_RUNNING after a crash. */
    public static ApplicationState appState() {
        return DriverFactory.driver().queryAppState(MobileConfig.appPackage());
    }

    /**
     * Waits briefly for <em>any</em> known system screen (one combined wait, so a clean launch costs
     * a single short probe) and dismisses it; repeats because they can appear one after another.
     */
    private static void dismissSystemScreens(AndroidDriver driver) {
        var probe = new WebDriverWait(driver, MobileConfig.dialogProbeTimeout());
        var anyScreen = ExpectedConditions.or(SYSTEM_SCREENS.stream()
                .map(screen -> ExpectedConditions.presenceOfElementLocated(screen.marker()))
                .toArray(ExpectedCondition[]::new));

        for (int dismissed = 0; dismissed < SYSTEM_SCREENS.size(); dismissed++) {
            try {
                probe.until(anyScreen);
            } catch (TimeoutException noneShown) {
                return;
            }
            SYSTEM_SCREENS.stream()
                    .filter(screen -> !driver.findElements(screen.marker()).isEmpty())
                    .findFirst()
                    .ifPresent(screen -> {
                        driver.findElement(screen.dismiss()).click();
                        LOG.info("Dismissed system screen: {}", screen.description());
                    });
        }
    }
}
