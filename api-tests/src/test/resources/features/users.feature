@api
Feature: Users API (reqres.in)
  As an API consumer
  I want to list and create users
  So that user data can be read and reused across requests

  @api_case1
  Scenario Outline: Find a user by id in the paginated users list
    When I request the users on page <page>
    Then the response status code should be 200
    And the users list should contain a user with id <id> and first name "<first name>"

    @smoke
    Examples: Task case - user 10 on page 2
      | page | id | first name |
      | 2    | 10 | Byron      |

    Examples: Additional users on page 2
      | page | id | first name |
      | 2    | 7  | Michael    |
      | 2    | 12 | Rachel     |

  @smoke @api_case2
  Scenario: Create a user from an existing user's data (API chaining)
    Given I have retrieved the user with id 10 from page 2
    When I create a new user from that user with job "BA"
    Then the response status code should be 201
    And the created user should have a non-empty id
    And the created user should echo the submitted name and job
    And the response body should match the schema "schemas/create-user-schema.json"
    And the created user's "createdAt" should be a valid ISO-8601 timestamp
