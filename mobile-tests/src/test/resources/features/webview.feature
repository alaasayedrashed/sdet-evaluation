@mobile
Feature: Hybrid web view screen
  The "Say Hello" demo is a web page served by the app and rendered in a WebView, so the scenario
  switches between the NATIVE_APP and WEBVIEW contexts.

  Background:
    Given the app is launched on the home screen

  @mobile_sc3 @screenshots
  Scenario: Send a name and preferred car through the web view
    When I tap the Chrome logo
    Then the screen title should be "selendroid-test-app"
    And the web view screen header should be "Web View Interaction"
    And the web page should display "Hello, can you please tell me your name?"
    When I enter the name "Alaa Rashed" in the web page
    And I select the preferred car "Mercedes" in the web page
    And I submit the web form with "Send me your name!"
    Then the web page should display "This is my way of saying hello"
    And the web page should show the name and car I entered
    When I click the "here" link in the web page
    Then the preferred car should default to "Volvo"
