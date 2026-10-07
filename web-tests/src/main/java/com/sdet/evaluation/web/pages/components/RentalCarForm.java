package com.sdet.evaluation.web.pages.components;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;


/**
 * Component object for one "Rental Car" controlgroup (used twice on the Controlgroup demo).
 *
 * <p>jQuery UI replaces the native controls with themed widgets, so the component drives what a
 * user actually sees: the selectmenu button and its popup list, the checkbox/radio labels and the
 * spinner arrows. Reads use the underlying native inputs, which jQuery UI keeps in sync.
 */
public class RentalCarForm {

    private static final String CHECKED = "ui-checkboxradio-checked";
    private static final int MAX_SPINNER_CLICKS = 50;

    private final FrameLocator frame;
    private final Locator root;
    private final String name;

    public RentalCarForm(FrameLocator frame, Locator root, String name) {
        this.frame = frame;
        this.root = root;
        this.name = name;
    }

    /** Opens the car-type selectmenu and picks an option by its visible text. */
    public void selectCarType(String carType) {
        Allure.step("[%s] Select car type \"%s\"".formatted(name, carType), () -> {
            Locator button = root.locator(".ui-selectmenu-button");
            button.click();
            // The popup list is appended to <body>; the button's aria-owns points at it
            String menuId = button.getAttribute("aria-owns");
            frame.locator("#" + menuId)
                    .getByRole(AriaRole.OPTION, new Locator.GetByRoleOptions().setName(carType).setExact(true))
                    .click();
        });
    }

    /** Selects a transmission radio button by its label, e.g. {@code Automatic}. */
    public void selectTransmission(String transmission) {
        Allure.step("[%s] Select transmission \"%s\"".formatted(name, transmission),
                () -> label(transmission).click());
    }

    /** Checks or unchecks the "Insurance" checkbox so that it ends in the requested state. */
    public void setInsurance(boolean insured) {
        Allure.step("[%s] Set insurance to %s".formatted(name, insured), () -> {
            if (isChecked("Insurance") != insured) {
                label("Insurance").click();
            }
        });
    }

    /** Sets the number of cars by clicking the spinner's up/down arrows, as a user would. */
    public void setNumberOfCars(int target) {
        Allure.step("[%s] Set number of cars to %d with the spinner".formatted(name, target), () -> {
            for (int clicks = 0; numberOfCars() != target; clicks++) {
                if (clicks >= MAX_SPINNER_CLICKS) {
                    throw new IllegalStateException("Spinner did not reach %d (stuck at %d)".formatted(target, numberOfCars()));
                }
                root.locator(numberOfCars() < target ? ".ui-spinner-up" : ".ui-spinner-down").click();
            }
        });
    }

    /** Clicks the form's "Book Now!" button. */
    public void bookNow() {
        Allure.step("[%s] Click \"Book Now!\"".formatted(name),
                () -> root.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Book Now!")).click());
    }

    /** Car type shown on the selectmenu button. */
    public String selectedCarType() {
        return root.locator(".ui-selectmenu-text").innerText().trim();
    }

    /** {@code true} if the given radio/checkbox (by label) is checked, both natively and visually. */
    public boolean isChecked(String labelText) {
        Locator label = label(labelText);
        Locator input = frame.locator("#" + label.getAttribute("for"));
        String labelClasses = label.getAttribute("class");
        return input.isChecked() && labelClasses != null && labelClasses.contains(CHECKED);
    }

    /** Current spinner value; an empty spinner counts as 0. */
    public int numberOfCars() {
        String value = root.locator("input.ui-spinner-input").inputValue().trim();
        return value.isEmpty() ? 0 : Integer.parseInt(value);
    }

    private Locator label(String text) {
        // Exact, whitespace-normalised text match (labels also contain icon spans)
        return root.locator("label").and(root.getByText(text, new Locator.GetByTextOptions().setExact(true)));
    }
}
