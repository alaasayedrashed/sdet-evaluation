@web
Feature: jQuery UI Widgets demos
  Every demo is opened from the left sidebar of jqueryui.com and exercised inside its demo iframe.

  Background:
    Given I am on the jQuery UI home page

  # ASSUMPTION: the task text for the Controlgroup case is cut off and names no actions.
  # We exercise every control in the "Rental Car" form - car type selectmenu, transmission radio,
  # insurance checkbox, number-of-cars spinner and "Book Now!" - in BOTH the horizontal and the
  # vertical controlgroup, then verify each control kept the chosen state.
  @web_case3
  Scenario Outline: Book a rental car with the <orientation> controlgroup
    Given I open the "Controlgroup" demo from the "Widgets" section of the sidebar
    When I fill the <orientation> rental car form with:
      | car type   | transmission   | insurance   | cars   |
      | <car type> | <transmission> | <insurance> | <cars> |
    And I click "Book Now!" in the <orientation> rental car form
    Then the <orientation> rental car form should show:
      | car type   | transmission   | insurance   | cars   |
      | <car type> | <transmission> | <insurance> | <cars> |

    Examples:
      | orientation | car type    | transmission | insurance | cars |
      | horizontal  | Compact car | Automatic    | true      | 2    |
      | vertical    | SUV         | Automatic    | true      | 2    |

  @smoke @web_case4
  Scenario: Pick today's date in the date picker
    Given I open the "Datepicker" demo from the "Widgets" section of the sidebar
    When I open the date picker
    Then the calendar should show the current month with today highlighted
    When I pick today's date
    Then the date field should contain today's date formatted as "MM/dd/yyyy"
