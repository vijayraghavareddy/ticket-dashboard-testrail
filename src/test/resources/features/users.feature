Feature: Users API validation
  As an API tester
  I want to validate users endpoints
  So that I can trust API responses

  Scenario: Get a single user
    Given the API base url is configured
    When I send a GET request to "/users/2"
    Then the response status code should be 200
    And the response body path "data.id" should be 2
    And the response body path "data.email" should be "janet.weaver@reqres.in"

  Scenario: Create a user
    Given the API base url is configured
    When I set request body to:
      """
      {
        "name": "morpheus",
        "job": "leader"
      }
      """
    And I send a POST request to "/users"
    Then the response status code should be 201
    And the response body path "name" should be "morpheus"
    And the response body path "job" should be "leader"
    And the response body path "id" should not be empty
    And the response should contain key "createdAt" in object path "$"
