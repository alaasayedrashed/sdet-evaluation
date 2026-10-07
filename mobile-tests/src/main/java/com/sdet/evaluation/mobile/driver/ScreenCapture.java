package com.sdet.evaluation.mobile.driver;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;

/**
 * Device screenshots that work in any context.
 *
 * <p>In a {@code WEBVIEW_*} context a screenshot request is proxied to Chromedriver, which only
 * captures the web page and can time out. Switching to {@code NATIVE_APP} for the capture gives a
 * fast, full-device screenshot (native header + web content); the original context is restored.
 */
public final class ScreenCapture {

    private static final String NATIVE_CONTEXT = "NATIVE_APP";

    private ScreenCapture() {
    }

    /** PNG screenshot of the whole device screen. */
    public static byte[] png(AndroidDriver driver) {
        String original = driver.getContext();
        boolean switched = original != null && !NATIVE_CONTEXT.equals(original);
        if (switched) {
            driver.context(NATIVE_CONTEXT);
        }
        try {
            return driver.getScreenshotAs(OutputType.BYTES);
        } finally {
            if (switched) {
                driver.context(original);
            }
        }
    }
}
