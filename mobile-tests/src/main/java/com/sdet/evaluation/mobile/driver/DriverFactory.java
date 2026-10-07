package com.sdet.evaluation.mobile.driver;

import com.sdet.evaluation.core.utils.FileUtils;
import com.sdet.evaluation.mobile.config.MobileConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Owns the {@link AndroidDriver} of the current thread.
 *
 * <p>Starting an Appium session is expensive (it installs/starts the UiAutomator2 server and the
 * app), so one session is reused by all scenarios on a thread and the <em>app</em> is reset
 * between scenarios instead (see {@code AppLauncher}). If a session dies, e.g. after the app
 * under test crashes badly, {@link #getOrCreate()} transparently replaces it so later scenarios
 * are not affected.
 */
public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    /**
     * Returns the thread's live session, creating a new one if there is none or the old one died.
     */
    public static AndroidDriver getOrCreate() {
        AndroidDriver driver = DRIVER.get();
        if (driver != null && isAlive(driver)) {
            return driver;
        }
        if (driver != null) {
            LOG.warn("Appium session {} is no longer usable; creating a new one", driver.getSessionId());
            quit();
        }
        driver = createDriver();
        DRIVER.set(driver);
        return driver;
    }

    /**
     * The thread's current session.
     *
     * @throws IllegalStateException if no session has been created on this thread
     */
    public static AndroidDriver driver() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("No Appium session on this thread; call DriverFactory.getOrCreate() first");
        }
        return driver;
    }

    /** {@code true} when a session exists on the current thread. */
    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    /**
     * Closes the app under test and ends the thread's session (if any), so nothing is left running
     * on the device. Errors from an already-dead session are ignored.
     */
    public static void quit() {
        AndroidDriver driver = DRIVER.get();
        DRIVER.remove();
        if (driver == null) {
            return;
        }
        try {
            driver.terminateApp(MobileConfig.appPackage());
        } catch (WebDriverException e) {
            LOG.warn("Could not close the app before ending the session: {}", e.getMessage());
        }
        try {
            driver.quit();
            LOG.info("Appium session ended");
        } catch (WebDriverException e) {
            LOG.warn("Ignoring error while quitting the Appium session: {}", e.getMessage());
        }
    }

    /** Builds the UiAutomator2 capabilities from {@code mobile.properties}. */
    static UiAutomator2Options capabilities() {
        var options = new UiAutomator2Options()
                .setPlatformName(MobileConfig.platformName())
                .setAutomationName(MobileConfig.automationName())
                .setDeviceName(MobileConfig.deviceName())
                .setApp(FileUtils.classpathResourcePath(MobileConfig.appPath()).toString())
                .setAppPackage(MobileConfig.appPackage())
                .setAppActivity(MobileConfig.appActivity())
                .setAutoGrantPermissions(MobileConfig.autoGrantPermissions())
                // Keep the installed app between sessions; scenarios reset it via terminate/activate
                .setNoReset(true)
                .setNewCommandTimeout(MobileConfig.newCommandTimeout())
                .setUiautomator2ServerInstallTimeout(MobileConfig.serverInstallTimeout())
                .setAdbExecTimeout(MobileConfig.adbExecTimeout())
                .setDisableWindowAnimation(true);
        // Popups (PopupWindow) live in their own window; expose every window in the page source
        options.amend("appium:enableMultiWindows", true);
        MobileConfig.udid().ifPresent(options::setUdid);
        MobileConfig.platformVersion().ifPresent(options::setPlatformVersion);
        return options;
    }

    private static AndroidDriver createDriver() {
        var options = capabilities();
        LOG.info("Starting Appium session at {} with {}", MobileConfig.appiumUrl(), options.asMap());
        try {
            AndroidDriver driver = new AndroidDriver(appiumUrl(), options);
            LOG.info("Appium session {} started on device {}", driver.getSessionId(),
                    driver.getCapabilities().getCapability("appium:deviceUDID"));
            return driver;
        } catch (SessionNotCreatedException e) {
            throw new IllegalStateException(("Could not start an Appium session at %s. Is the Appium server running "
                    + "and an emulator/device connected (adb devices)? Cause: %s").formatted(MobileConfig.appiumUrl(), e.getMessage()), e);
        }
    }

    private static boolean isAlive(AndroidDriver driver) {
        try {
            driver.getSessionId();
            driver.getStatus();
            driver.queryAppState(MobileConfig.appPackage());
            return true;
        } catch (WebDriverException e) {
            return false;
        }
    }

    private static URL appiumUrl() {
        try {
            return URI.create(MobileConfig.appiumUrl()).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new IllegalStateException("Invalid mobile.appium.url: " + MobileConfig.appiumUrl(), e);
        }
    }
}
