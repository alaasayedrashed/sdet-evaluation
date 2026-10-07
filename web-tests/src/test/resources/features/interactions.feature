@web
Feature: jQuery UI Interactions demos
  Every demo is opened from the left sidebar of jqueryui.com and exercised inside its demo iframe.
  Note: the task lists Resizable and Sortable under "Widgets", but jqueryui.com lists them under
  "Interactions", so the sidebar section used here is "Interactions".

  Background:
    Given I am on the jQuery UI home page

  @smoke @web_case1 @screenshots
  Scenario: Drop the draggable box onto the target
    Given I open the "Droppable" demo from the "Interactions" section of the sidebar
    When I drag the draggable box onto the drop target
    Then the drop target should display "Dropped!"
    And the drop target should have the "ui-state-highlight" class

  @web_case2
  Scenario: Select several items with Ctrl+click
    Given I open the "Selectable" demo from the "Interactions" section of the sidebar
    When I select the following items while holding Ctrl:
      | Item 1 |
      | Item 3 |
      | Item 7 |
    Then only the following items should be selected:
      | Item 1 |
      | Item 3 |
      | Item 7 |

  @web_case5
  Scenario: Resize the box with the bottom-right handle
    Given I open the "Resizable" demo from the "Interactions" section of the sidebar
    When I resize the box by dragging its bottom-right handle by 120 x 80 pixels
    Then the box should have grown by about 120 x 80 pixels within 5 pixels tolerance

  @web_case6 @screenshots
  Scenario: Reorder the sortable list from ascending to descending
    Given I open the "Sortable" demo from the "Interactions" section of the sidebar
    And the sortable items should be in this order:
      | Item 1 |
      | Item 2 |
      | Item 3 |
      | Item 4 |
      | Item 5 |
      | Item 6 |
      | Item 7 |
    When I reverse the sortable list by dragging the items
    Then the sortable items should be in this order:
      | Item 7 |
      | Item 6 |
      | Item 5 |
      | Item 4 |
      | Item 3 |
      | Item 2 |
      | Item 1 |
