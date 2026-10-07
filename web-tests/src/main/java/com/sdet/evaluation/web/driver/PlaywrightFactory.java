package com.sdet.evaluation.web.driver;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.sdet.evaluation.core.config.ConfigurationException;
import com.sdet.evaluation.core.utils.FileUtils;
import com.sdet.evaluation.web.config.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Creates and tears down the Playwright stack (Playwright -> Browser -> BrowserContext -> Page)
 * for the current thread.
 *
 * <p>Each object lives in a {@link ThreadLocal}, so scenarios running in parallel threads never
 * share a browser. Every scenario gets a brand-new stack: slightly slower than reusing the
 * browser, but it guarantees isolation (cookies, storage, open dialogs) and keeps teardown simple.
 */
public final class PlaywrightFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PlaywrightFactory.class);

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private PlaywrightFactory() {
    }

    /**
     * Launches the configured browser and opens a fresh page for the current thread.
     *
     * @return the new page
     */
    public static Page start() {
        if (PAGE.get() != null) {
            LOG.warn("A page is already open on this thread; closing it before starting a new one");
            stop();
        }
        Playwright playwright = Playwright.create();
        PLAYWRIGHT.set(playwright);

        Browser browser = browserType(playwright).launch(new BrowserType.LaunchOptions()
                .setHeadless(WebConfig.headless())
                .setSlowMo(WebConfig.slowMo().toMillis()));
        BROWSER.set(browser);

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(WebConfig.viewportWidth(), WebConfig.viewportHeight()));
        context.setDefaultTimeout(WebConfig.defaultTimeout().toMillis());
        context.setDefaultNavigationTimeout(WebConfig.navigationTimeout().toMillis());
        if (WebConfig.traceOnFailure()) {
            context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
        }
        CONTEXT.set(context);

        Page page = context.newPage();
        PAGE.set(page);
        LOG.info("Started {} {} (headless={})", WebConfig.browser(), browser.version(), WebConfig.headless());
        return page;
    }

    /**
     * The page of the current thread.
     *
     * @throws IllegalStateException if {@link #start()} has not been called on this thread
     */
    public static Page page() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException("No Playwright page on this thread; call PlaywrightFactory.start() first");
        }
        return page;
    }

    /** {@code true} when a page is open on the current thread. */
    public static boolean isStarted() {
        return PAGE.get() != null;
    }

    /**
     * Stops tracing. When {@code keep} is true the trace is saved as a zip that can be opened with
     * {@code npx playwright show-trace <file>} or at trace.playwright.dev; otherwise it is discarded.
     *
     * @return the saved trace file, if one was written
     */
    public static Optional<Path> stopTracing(boolean keep, String fileName) {
        BrowserContext context = CONTEXT.get();
        if (context == null || !WebConfig.traceOnFailure()) {
            return Optional.empty();
        }
        try {
            if (!keep) {
                context.tracing().stop();
                return Optional.empty();
            }
            Path trace = FileUtils.ensureDirectory(WebConfig.artifactsDir().resolve("traces")).resolve(fileName);
            context.tracing().stop(new Tracing.StopOptions().setPath(trace));
            LOG.info("Saved Playwright trace to {}", trace.toAbsolutePath());
            return Optional.of(trace);
        } catch (RuntimeException e) {
            LOG.warn("Could not stop tracing: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /** Closes the page, context, browser and Playwright of the current thread, ignoring close errors. */
    public static void stop() {
        closeQuietly("context", CONTEXT.get());
        closeQuietly("browser", BROWSER.get());
        closeQuietly("playwright", PLAYWRIGHT.get());
        PAGE.remove();
        CONTEXT.remove();
        BROWSER.remove();
        PLAYWRIGHT.remove();
    }

    private static BrowserType browserType(Playwright playwright) {
        return switch (WebConfig.browser()) {
            case "chromium" -> playwright.chromium();
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> throw new ConfigurationException(
                    "Unsupported web.browser '%s' (use chromium, firefox or webkit)".formatted(WebConfig.browser()));
        };
    }

    private static void closeQuietly(String name, AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            LOG.warn("Ignoring error while closing {}: {}", name, e.getMessage());
        }
    }
}
