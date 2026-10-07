@mobile
Feature: Home screen of the selendroid test app
  The app is relaunched before every scenario, so each one starts on the home screen.

  Background:
    Given the app is launched on the home screen

  @smoke @mobile_sc1
  Scenario: Home screen shows its title and key elements
    Then the screen title should be "selendroid-test-app"
    And all key home screen elements should be displayed

  @mobile_sc2
  Scenario: Cancelling the EN Button dialog keeps the user on the home screen
    When I tap the EN Button
    And I choose "No, no" in the dialog
    Then the home screen should be displayed

  @mobile_sc6
  Scenario: A toast message is displayed
    When I tap "Displays a Toast" to show a toast
    Then a toast with the text "Hello selendroid toast!" should be shown

  @mobile_sc7
  Scenario: A popup window can be dismissed
    When I tap "Display Popup Window" to open the popup
    Then the popup window should be displayed
    When I dismiss the popup window
    Then the popup window should be closed
