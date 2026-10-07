package com.sdet.evaluation.web.steps;

import com.sdet.evaluation.web.pages.DroppablePage;
import com.sdet.evaluation.web.pages.ResizablePage;
import com.sdet.evaluation.web.pages.ResizablePage.Size;
import com.sdet.evaluation.web.pages.SelectablePage;
import com.sdet.evaluation.web.pages.SortablePage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Steps for the Interactions demos: Droppable, Selectable, Resizable and Sortable.
 */
public class InteractionsSteps {

    private final DroppablePage droppablePage;
    private final SelectablePage selectablePage;
    private final ResizablePage resizablePage;
    private final SortablePage sortablePage;

    /** Size before resizing; step instances live for one scenario, so this is scenario-scoped. */
    private Size sizeBeforeResize;

    public InteractionsSteps(DroppablePage droppablePage, SelectablePage selectablePage,
                             ResizablePage resizablePage, SortablePage sortablePage) {
        this.droppablePage = droppablePage;
        this.selectablePage = selectablePage;
        this.resizablePage = resizablePage;
        this.sortablePage = sortablePage;
    }

    // ---------- Droppable ----------

    @When("I drag the draggable box onto the drop target")
    public void dragBoxOntoTarget() {
        droppablePage.dragBoxOntoTarget();
    }

    @Then("the drop target should display {string}")
    public void dropTargetShouldDisplay(String expectedText) {
        assertThat(droppablePage.dropTargetText()).as("drop target text after the drop").isEqualTo(expectedText);
    }

    @Then("the draggable box should be inside the drop target")
    public void draggableShouldBeInsideTarget() {
        assertThat(droppablePage.isDraggableInsideTarget()).as("draggable box lies within the drop target").isTrue();
    }

    @Then("the drop target should have the {string} class")
    public void dropTargetShouldHaveClass(String cssClass) {
        assertThat(droppablePage.dropTargetHasClass(cssClass))
                .as("drop target should be highlighted with class '%s'", cssClass)
                .isTrue();
    }

    // ---------- Selectable ----------

    @When("I select the following items while holding Ctrl:")
    public void selectItems(List<String> items) {
        selectablePage.selectItems(items);
    }

    @Then("only the following items should be selected:")
    public void onlyItemsShouldBeSelected(List<String> expected) {
        assertThat(selectablePage.selectedItems())
                .as("items with the 'ui-selected' class (all items: %s)", selectablePage.allItems())
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    // ---------- Resizable ----------

    @When("I resize the box by dragging its bottom-right handle by {int} x {int} pixels")
    public void resizeBox(int dx, int dy) {
        sizeBeforeResize = resizablePage.boxSize();
        resizablePage.resizeBy(dx, dy);
    }

    @Then("the box should have grown by about {int} x {int} pixels within {int} pixels tolerance")
    public void boxShouldHaveGrown(int dx, int dy, int tolerance) {
        Size after = resizablePage.boxSize();

        assertThat(after.width() - sizeBeforeResize.width())
                .as("width growth (before %s, after %s)", sizeBeforeResize, after)
                .isCloseTo(dx, within((double) tolerance));
        assertThat(after.height() - sizeBeforeResize.height())
                .as("height growth (before %s, after %s)", sizeBeforeResize, after)
                .isCloseTo(dy, within((double) tolerance));
    }

    // ---------- Sortable ----------

    @Then("the sortable items should be in this order:")
    public void itemsShouldBeInOrder(List<String> expectedOrder) {
        assertThat(sortablePage.itemOrder()).as("sortable list order").containsExactlyElementsOf(expectedOrder);
    }

    @When("I reverse the sortable list by dragging the items")
    public void reverseSortableList() {
        sortablePage.reverseOrder();
    }
}
