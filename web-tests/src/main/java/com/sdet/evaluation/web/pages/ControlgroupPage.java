package com.sdet.evaluation.web.pages;

import com.sdet.evaluation.web.pages.components.RentalCarForm;

/**
 * Widgets / Controlgroup demo: two identical "Rental Car" forms, one laid out horizontally and
 * one vertically. Each form is modelled as a reusable {@link RentalCarForm} component.
 */
public class ControlgroupPage extends BasePage {

    /** The two layouts shown by the demo, identified by their container CSS class. */
    public enum Orientation {
        HORIZONTAL(".controlgroup"),
        VERTICAL(".controlgroup-vertical");

        private final String containerSelector;

        Orientation(String containerSelector) {
            this.containerSelector = containerSelector;
        }
    }

    /** The rental-car form rendered with the given orientation. */
    public RentalCarForm form(Orientation orientation) {
        return new RentalCarForm(demoFrame(), demoFrame().locator(orientation.containerSelector), orientation.name());
    }
}
