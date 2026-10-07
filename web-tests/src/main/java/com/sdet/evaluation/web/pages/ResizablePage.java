package com.sdet.evaluation.web.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.BoundingBox;
import io.qameta.allure.Step;

/** Interactions / Resizable demo. */
public class ResizablePage extends BasePage {

  /** Rendered size of the resizable box in CSS pixels. */
  public record Size(double width, double height) {}

  private Locator box() {
    return demoFrame().locator("#resizable");
  }

  private Locator bottomRightHandle() {
    return box().locator(".ui-resizable-se");
  }

  /** Current rendered size of the box. */
  public Size boxSize() {
    BoundingBox box = boundingBox(box());
    return new Size(box.width, box.height);
  }

  @Step("Drag the bottom-right resize handle by ({dx}, {dy}) px")
  public void resizeBy(int dx, int dy) {
    dragBy(bottomRightHandle(), dx, dy);
  }
}
