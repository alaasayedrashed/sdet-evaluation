package com.sdet.evaluation.web.pages;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Mouse;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import com.sdet.evaluation.web.config.WebConfig;
import com.sdet.evaluation.web.driver.PlaywrightFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Base class for every jqueryui.com page object.
 *
 * <p>Provides the demo iframe ({@link #demoFrame()}), mouse-driven drag helpers that emit real
 * {@code mousemove} events (jQuery UI ignores synthetic HTML5 drag events), and DOM read helpers.
 * All waiting is delegated to Playwright's auto-waiting with the configured timeout, so no page
 * object ever sleeps.
 *
 * <p>The {@link Page} is resolved lazily from {@link PlaywrightFactory} on every call, so page
 * objects can be created by dependency injection before the browser is started.
 */
public abstract class BasePage {

    private static final String DEMO_FRAME = "iframe.demo-frame";
    /** Small first move that crosses jQuery UI's drag-start threshold before the real move. */
    private static final double DRAG_START_NUDGE = 5;

    /** A point in page (viewport) coordinates. */
    protected record Point(double x, double y) {
    }

    /** The current thread's Playwright page. */
    protected Page page() {
        return PlaywrightFactory.page();
    }

    /** Every jqueryui.com demo renders inside this iframe; all demo interactions must go through it. */
    protected FrameLocator demoFrame() {
        return page().frameLocator(DEMO_FRAME);
    }

    /** Drags {@code source} by its centre and drops it on the centre of {@code target}. */
    protected void dragAndDrop(Locator source, Locator target) {
        dragTo(source, center(target));
    }

    /** Drags {@code handle} by its centre to a point offset by {@code (dx, dy)} pixels. */
    protected void dragBy(Locator handle, double dx, double dy) {
        Point from = center(handle);
        dragFromTo(from, new Point(from.x() + dx, from.y() + dy));
    }

    /** Drags {@code source} by its centre to an absolute page point. */
    protected void dragTo(Locator source, Point destination) {
        dragFromTo(center(source), destination);
    }

    /** Centre of an element in page coordinates (scrolling it into view first). */
    protected Point center(Locator locator) {
        BoundingBox box = boundingBox(locator);
        return new Point(box.x + box.width / 2, box.y + box.height / 2);
    }

    /**
     * Bounding box in page coordinates, which already includes the iframe offset.
     *
     * @throws IllegalStateException if the element is not rendered
     */
    protected BoundingBox boundingBox(Locator locator) {
        locator.scrollIntoViewIfNeeded();
        BoundingBox box = locator.boundingBox();
        if (box == null) {
            throw new IllegalStateException("Element is not visible, no bounding box: " + locator);
        }
        return box;
    }

    /** The element's CSS classes. */
    protected List<String> classesOf(Locator locator) {
        String classes = locator.getAttribute("class");
        return classes == null ? List.of() : Arrays.asList(classes.trim().split("\\s+"));
    }

    /** {@code true} if the element currently has the given CSS class. */
    protected boolean hasClass(Locator locator, String cssClass) {
        return classesOf(locator).contains(cssClass);
    }

    /** Computed CSS value as the browser renders it, e.g. {@code rgb(64, 250, 8)}. */
    protected String computedStyle(Locator locator, String property) {
        return (String) locator.evaluate(
                "(element, property) => getComputedStyle(element).getPropertyValue(property)", property);
    }

    /** Visible text of every element matched by the locator, trimmed, in DOM order. */
    protected List<String> texts(Locator locator) {
        return locator.allInnerTexts().stream().map(String::trim).toList();
    }

    private void dragFromTo(Point from, Point to) {
        Mouse mouse = page().mouse();
        mouse.move(from.x(), from.y());
        mouse.down();
        mouse.move(from.x() + DRAG_START_NUDGE, from.y() + DRAG_START_NUDGE);
        mouse.move(to.x(), to.y(), new Mouse.MoveOptions().setSteps(WebConfig.dragSteps()));
        mouse.up();
    }
}
