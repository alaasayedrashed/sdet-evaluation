@mobile @negative
Feature: Unhandled exceptions (intentional failures)
  These scenarios are EXPECTED TO FAIL: the app crashes on purpose, so the home screen title can
  no longer be verified. They demonstrate how the framework reports a failure (clear assertion
  message, screenshot, page source, logcat) and recovers (the next scenario relaunches the app).
  They are excluded from the default run; execute them with -Dcucumber.filter.tags="@negative".

  Background:
    Given the app is launched on the home screen

  @mobile_sc8
  Scenario: Pressing the exception button crashes the app
    When I tap "Press to throw unhandled exception" to throw an unhandled exception
    Then the home screen should be displayed with the title "selendroid-test-app"

  @mobile_sc9
  Scenario: Typing into the exception field crashes the app
    When I type "test" into the exception test field
    Then the home screen should be displayed with the title "selendroid-test-app"
