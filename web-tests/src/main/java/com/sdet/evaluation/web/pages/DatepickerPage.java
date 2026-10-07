package com.sdet.evaluation.web.pages;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

/** Widgets / Datepicker demo. */
public class DatepickerPage extends BasePage {

  private static final String TODAY_CELL_CLASS = "ui-datepicker-today";
  private static final String HIGHLIGHT_CLASS = "ui-state-highlight";
  private static final char NO_BREAK_SPACE = ' ';

  private Locator dateInput() {
    return demoFrame().locator("#datepicker");
  }

  private Locator calendar() {
    return demoFrame().locator("#ui-datepicker-div");
  }

  /**
   * Calendar cell for a date. jQuery UI tags day cells with {@code data-month} (0-based) and {@code
   * data-year}, so the cell is found by date rather than by grid position.
   */
  private Locator dayCell(LocalDate date) {
    return calendar()
        .locator(
            "td[data-year='%d'][data-month='%d']"
                .formatted(date.getYear(), date.getMonthValue() - 1))
        .filter(
            new Locator.FilterOptions()
                .setHas(demoFrame().locator("a:text-is('%d')".formatted(date.getDayOfMonth()))));
  }

  @Step("Open the date picker")
  public void openCalendar() {
    dateInput().click();
    calendar().waitFor();
  }

  /** Month and year shown in the calendar header, e.g. {@code October 2026}. */
  public String displayedMonth() {
    // The header separates month and year with a non-breaking space
    return calendar()
        .locator(".ui-datepicker-title")
        .innerText()
        .replace(NO_BREAK_SPACE, ' ')
        .trim();
  }

  /** Expected header text for a date, e.g. {@code October 2026}. */
  public static String monthTitle(LocalDate date) {
    return date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + date.getYear();
  }

  /** {@code true} if the cell of {@code date} is marked as today and visually highlighted. */
  public boolean isHighlightedAsToday(LocalDate date) {
    Locator cell = dayCell(date);
    return hasClass(cell, TODAY_CELL_CLASS) && hasClass(cell.locator("a"), HIGHLIGHT_CLASS);
  }

  @Step("Pick {date} in the calendar")
  public void pickDate(LocalDate date) {
    dayCell(date).locator("a").click();
  }

  /** Value of the date input. */
  public String inputValue() {
    return dateInput().inputValue();
  }
}
