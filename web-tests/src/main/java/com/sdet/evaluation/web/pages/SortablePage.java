package com.sdet.evaluation.web.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.BoundingBox;
import io.qameta.allure.Step;

import java.util.List;

/**
 * Interactions / Sortable demo.
 */
public class SortablePage extends BasePage {

    /** Drop point inside the first item: its upper quarter, so the sortable inserts *before* it. */
    private static final double UPPER_QUARTER = 0.25;

    private Locator items() {
        return demoFrame().locator("#sortable li");
    }

    /** Item texts in their current display order. */
    public List<String> itemOrder() {
        return texts(items());
    }

    /**
     * Reverses the list with real mouse drags: the item currently at the bottom is moved to the
     * top until every item has been moved once ({@code 1..n -> n..1}). Each move is a genuine
     * press / move-in-steps / release, which is what jQuery UI Sortable listens to.
     */
    @Step("Reverse the list order by dragging items")
    public void reverseOrder() {
        int count = items().count();
        for (int moved = 0; moved < count - 1; moved++) {
            Locator lastItem = items().nth(count - 1);
            moveToPosition(lastItem, moved);
        }
    }

    /** Drags an item so that it is inserted before the item currently at {@code index}. */
    @Step("Drag item to position {index}")
    public void moveToPosition(Locator item, int index) {
        BoundingBox target = boundingBox(items().nth(index));
        dragTo(item, new Point(target.x + target.width / 2, target.y + target.height * UPPER_QUARTER));
    }
}
