package com.sdet.evaluation.web.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.BoundingBox;
import io.qameta.allure.Step;

/**
 * Interactions / Droppable demo.
 */
public class DroppablePage extends BasePage {

    private Locator draggable() {
        return demoFrame().locator("#draggable");
    }

    private Locator dropTarget() {
        return demoFrame().locator("#droppable");
    }

    @Step("Drag the draggable box onto the drop target")
    public void dragBoxOntoTarget() {
        dragAndDrop(draggable(), dropTarget());
    }

    /** Text displayed inside the drop target. */
    public String dropTargetText() {
        return dropTarget().innerText().trim();
    }

    /** {@code true} if the draggable box now lies within the drop target's bounds. */
    public boolean isDraggableInsideTarget() {
        BoundingBox box = boundingBox(draggable());
        BoundingBox target = boundingBox(dropTarget());
        return box.x >= target.x && box.y >= target.y
                && box.x + box.width <= target.x + target.width
                && box.y + box.height <= target.y + target.height;
    }

    /** {@code true} if the drop target has the given CSS class, e.g. {@code ui-state-highlight}. */
    public boolean dropTargetHasClass(String cssClass) {
        return hasClass(dropTarget(), cssClass);
    }
}
