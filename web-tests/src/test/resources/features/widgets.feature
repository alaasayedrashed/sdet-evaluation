@web
Feature: jQuery UI Widgets demos
  Every demo is opened from the left sidebar of jqueryui.com and exercised inside its demo iframe.

  Background:
    Given I am on the jQuery UI home page

  # The actions follow the reference image in the task: both "Rental Car" groups filled as shown,
  # then "Book Now!" in the vertical group.
  @web_case3
  Scenario: Fill both rental car controlgroups as shown in the task
    Given I open the "Controlgroup" demo from the "Widgets" section of the sidebar
    When I fill the horizontal rental car form with:
      | car type | transmission | insurance | cars |
      | SUV      | Automatic    | true      | 2    |
    And I fill the vertical rental car form with:
      | car type | transmission | insurance | cars |
      | Truck    | Standard     | true      | 1    |
    And I click "Book Now!" in the vertical rental car form
    Then the horizontal rental car form should show:
      | car type | transmission | insurance | cars |
      | SUV      | Automatic    | true      | 2    |
    And the vertical rental car form should show:
      | car type | transmission | insurance | cars |
      | Truck    | Standard     | true      | 1    |

  @smoke @web_case4
  Scenario: Pick today's date in the date picker
    Given I open the "Datepicker" demo from the "Widgets" section of the sidebar
    When I open the date picker
    Then the calendar should show the current month with today highlighted
    When I pick today's date
    Then the date field should contain today's date formatted as "MM/dd/yyyy"
