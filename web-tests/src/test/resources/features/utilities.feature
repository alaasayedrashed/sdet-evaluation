@web
Feature: jQuery UI Utilities demos
  Note: the task lists Widget Factory under "Widgets", but jqueryui.com lists it under
  "Utilities", so the sidebar section used here is "Utilities".

  Background:
    Given I am on the jQuery UI home page

  # The demo's "Go green" handler sets red=64, green=250, blue=8 on every colorize widget.
  @web_case7
  Scenario: Turn every colorize widget green
    Given I open the "Widget Factory" demo from the "Utilities" section of the sidebar
    When I click the "Go green" button
    Then every colorized widget should have the background color "rgb(64, 250, 8)"
