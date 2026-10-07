@api
Feature: Users API (reqres.in)
  As an API consumer
  I want to list and create users
  So that user data can be read and reused across requests

  @smoke @api_case1
  Scenario: Get the users on page 2 and check user 10
    When I request the users on page 2
    Then the response status code should be 200
    And the users list should contain a user with id 10 and first name "Byron"

  @smoke @api_case2
  Scenario: Create a user from an existing user's data (API chaining)
    Given I have retrieved the user with id 10 from page 2
    When I create a new user from that user with job "BA"
    Then the response status code should be 201
    And the created user should have a non-empty id
    And the created user should echo the submitted name and job
    And the response body should match the schema "schemas/create-user-schema.json"
