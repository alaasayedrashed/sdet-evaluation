package com.sdet.evaluation.web.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.KeyboardModifier;
import io.qameta.allure.Step;
import java.util.List;

/** Interactions / Selectable demo. */
public class SelectablePage extends BasePage {

  private static final String SELECTED = "ui-selected";

  private Locator items() {
    return demoFrame().locator("#selectable li");
  }

  private Locator item(String text) {
    return items().filter(new Locator.FilterOptions().setHasText(text));
  }

  /**
   * Selects several items like a user would: Ctrl+click (Cmd+click on macOS) on each one, so
   * earlier selections are kept.
   */
  @Step("Ctrl/Cmd+click items {itemTexts}")
  public void selectItems(List<String> itemTexts) {
    var clickWithModifier =
        new Locator.ClickOptions().setModifiers(List.of(KeyboardModifier.CONTROLORMETA));
    itemTexts.forEach(text -> item(text).click(clickWithModifier));
  }

  /** Texts of the items currently marked as selected. */
  public List<String> selectedItems() {
    return texts(demoFrame().locator("#selectable li." + SELECTED));
  }

  /** Texts of all items, in display order. */
  public List<String> allItems() {
    return texts(items());
  }
}
