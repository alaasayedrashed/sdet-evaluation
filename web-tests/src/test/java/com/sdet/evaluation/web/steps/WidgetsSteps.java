package com.sdet.evaluation.web.steps;

import com.sdet.evaluation.core.utils.DateUtils;
import com.sdet.evaluation.web.pages.ControlgroupPage;
import com.sdet.evaluation.web.pages.ControlgroupPage.Orientation;
import com.sdet.evaluation.web.pages.DatepickerPage;
import com.sdet.evaluation.web.pages.WidgetFactoryPage;
import com.sdet.evaluation.web.pages.components.RentalCarForm;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * Steps for the Widgets demos (Controlgroup, Datepicker) and the Widget Factory utility.
 */
public class WidgetsSteps {

    /** One rental-car booking, built from a two-column Gherkin table. */
    public record RentalCarBooking(String carType, String transmission, boolean insurance, int cars) {
    }

    private final ControlgroupPage controlgroupPage;
    private final DatepickerPage datepickerPage;
    private final WidgetFactoryPage widgetFactoryPage;

    /** Today's date, fixed once per scenario so every step uses the same value. */
    private final LocalDate today = DateUtils.today();

    public WidgetsSteps(ControlgroupPage controlgroupPage, DatepickerPage datepickerPage,
                        WidgetFactoryPage widgetFactoryPage) {
        this.controlgroupPage = controlgroupPage;
        this.datepickerPage = datepickerPage;
        this.widgetFactoryPage = widgetFactoryPage;
    }

    @ParameterType("horizontal|vertical")
    public Orientation orientation(String value) {
        return Orientation.valueOf(value.toUpperCase(Locale.ROOT));
    }

    @DataTableType
    public RentalCarBooking rentalCarBooking(Map<String, String> row) {
        return new RentalCarBooking(
                row.get("car type"),
                row.get("transmission"),
                Boolean.parseBoolean(row.get("insurance")),
                Integer.parseInt(row.get("cars")));
    }

    // ---------- Controlgroup ----------

    @When("I fill the {orientation} rental car form with:")
    public void fillRentalCarForm(Orientation orientation, RentalCarBooking booking) {
        RentalCarForm form = controlgroupPage.form(orientation);
        form.selectCarType(booking.carType());
        form.selectTransmission(booking.transmission());
        form.setInsurance(booking.insurance());
        form.setNumberOfCars(booking.cars());
    }

    @When("I click {string} in the {orientation} rental car form")
    public void clickBookNow(String button, Orientation orientation) {
        assertThat(button).as("only the 'Book Now!' button is modelled").isEqualTo("Book Now!");
        controlgroupPage.form(orientation).bookNow();
    }

    @Then("the {orientation} rental car form should show:")
    public void rentalCarFormShouldShow(Orientation orientation, RentalCarBooking expected) {
        RentalCarForm form = controlgroupPage.form(orientation);

        assertSoftly(softly -> {
            softly.assertThat(form.selectedCarType()).as("%s form: car type", orientation).isEqualTo(expected.carType());
            softly.assertThat(form.isChecked(expected.transmission()))
                    .as("%s form: transmission '%s' selected", orientation, expected.transmission()).isTrue();
            softly.assertThat(form.isChecked("Insurance")).as("%s form: insurance", orientation).isEqualTo(expected.insurance());
            softly.assertThat(form.numberOfCars()).as("%s form: number of cars", orientation).isEqualTo(expected.cars());
        });
    }

    // ---------- Datepicker ----------

    @When("I open the date picker")
    public void openDatePicker() {
        datepickerPage.openCalendar();
    }

    @Then("the calendar should show the current month with today highlighted")
    public void calendarShouldHighlightToday() {
        assertThat(datepickerPage.displayedMonth())
                .as("month shown when the picker opens")
                .isEqualTo(DatepickerPage.monthTitle(today));
        assertThat(datepickerPage.isHighlightedAsToday(today))
                .as("cell for today (%s) should have 'ui-datepicker-today' and 'ui-state-highlight'", today)
                .isTrue();
    }

    @When("I pick today's date")
    public void pickToday() {
        datepickerPage.pickDate(today);
    }

    @Then("the date field should contain today's date formatted as {string}")
    public void dateFieldShouldContainToday(String pattern) {
        assertThat(datepickerPage.inputValue())
                .as("date input value for today (%s)", today)
                .isEqualTo(DateUtils.format(today, pattern));
    }

    // ---------- Widget Factory ----------

    @When("I click the {string} button")
    public void clickButton(String label) {
        widgetFactoryPage.clickButton(label);
    }

    @Then("every colorized widget should have the background color {string}")
    public void widgetsShouldHaveColor(String expectedColor) {
        List<String> colors = widgetFactoryPage.widgetBackgroundColors();

        assertThat(colors).as("computed background-color of each colorized widget").isNotEmpty().allMatch(expectedColor::equals,
                "equal to " + expectedColor);
    }
}
