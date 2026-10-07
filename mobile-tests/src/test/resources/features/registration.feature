@mobile
Feature: User registration

  Background:
    Given the app is launched on the home screen

  @smoke @mobile_sc4
  Scenario: Register a new user and confirm the details
    When I tap the File logo
    Then the screen title should be "selendroid-test-app"
    And the registration screen should show "Welcome to register a new User"
    And all registration form elements should be displayed
    And the Name field should be pre-filled with "Mr. Burns"
    And the default programming language should be "Ruby"
    When I fill the registration form with:
      | username | email               | password   | name        | programming language | accept adds |
      | alaa_qa  | alaa.qa@example.com | Secret#123 | Alaa Rashed | Java                 | true        |
    And I tap Register User
    Then the confirmation screen should show the details I entered
    When I confirm the registration with Register User
    Then the home screen should be displayed

  @mobile_sc5
  Scenario: The registration screen opens after the progress bar finishes
    When I tap "Show Progress Bar for a while" and wait for the loader to disappear
    Then the registration screen should show "Welcome to register a new User"
    And all registration form elements should be displayed
